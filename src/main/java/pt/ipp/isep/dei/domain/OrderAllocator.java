package main.java.pt.ipp.isep.dei.domain;

import java.util.*;
import java.util.stream.Collectors;

public class OrderAllocator {

    public static class Result {
        public final List<LineStatus> lineStatuses;
        public final List<AllocationRow> allocationRows;

        public Result(List<LineStatus> lineStatuses, List<AllocationRow> allocationRows) {
            this.lineStatuses = lineStatuses;
            this.allocationRows = allocationRows;
        }
    }

    /**
     * modePartial = false -> strict mode (default)
     * modePartial = true -> allow partial line allocations
     */
    public Result allocateOrders(List<Order> orders, Warehouse warehouse, boolean modePartial) {
        // 1) Preparar mapa boxId -> Bay (para obter aisle/bay ao devolver alocação)
        Map<String, Bay> boxToBay = new HashMap<>();
        Map<String, Integer> remainingPerBox = new HashMap<>(); // planning quantities

        for (Bay bay : warehouse.getBays().values()) {
            for (Box b : bay.getStoredBoxes()) {
                boxToBay.put(b.getBoxId(), bay);
                // Se houver duplicados de boxId entre bays, assume único; último sobrescreve (não deve ocorrer).
                remainingPerBox.put(b.getBoxId(), b.getQuantity());
            }
        }

        // 2) Lista de boxes por SKU — usaremos sempre a ordem FEFO definida:
        // FEFO comparator: expiryDate null last, expiryDate asc, receivedAt asc, boxId asc
        Comparator<Box> fefoComparator = Comparator
                .comparing((Box b) -> b.getExpiryDate(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Box::getReceivedAt)
                .thenComparing(Box::getBoxId);

        // Helper: get all boxes for SKU sorted FEFO
        Map<String, List<Box>> skuToBoxesSorted = new HashMap<>();
        for (Bay bay : warehouse.getBays().values()) {
            for (Box b : bay.getStoredBoxes()) {
                skuToBoxesSorted.computeIfAbsent(b.getSku(), k -> new ArrayList<>()).add(b);
            }
        }
        // sort lists
        skuToBoxesSorted.values().forEach(list -> list.sort(fefoComparator));

        // 3) Ordenar orders (priority ASC, dueDate ASC, orderId ASC)
        List<Order> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(Comparator
                .comparingInt(Order::getPriority)
                .thenComparing(Order::getDueDate)
                .thenComparing(Order::getOrderId));

        List<LineStatus> statuses = new ArrayList<>();
        List<AllocationRow> allocations = new ArrayList<>();

        // 4) Processar cada order e suas linhas
        for (Order order : sortedOrders) {
            // lines should be in lineNo ASC (assume order.getLines() may be already in input order, but let's ensure)
            List<OrderLine> lines = new ArrayList<>(order.getLines());
            lines.sort(Comparator.comparingInt(OrderLine::getLineNo));

            for (OrderLine line : lines) {
                String sku = line.getSku();
                int remaining = line.getQtyRequested();
                int allocatedForLine = 0;

                // Prepare temporary list to record allocations for this line (to rollback if strict and not full)
                List<AllocationRow> lineAllocations = new ArrayList<>();
                Map<String, Integer> boxUsageThisLine = new HashMap<>(); // boxId -> usedQty

                // Walk boxes in FEFO order for this SKU
                List<Box> candidateBoxes = skuToBoxesSorted.getOrDefault(sku, Collections.emptyList());

                for (Box box : candidateBoxes) {
                    if (remaining <= 0) break;
                    String boxId = box.getBoxId();
                    Integer avail = remainingPerBox.getOrDefault(boxId, 0);
                    if (avail <= 0) continue;

                    int take = Math.min(remaining, avail);
                    if (take <= 0) continue;

                    // record
                    Bay bay = boxToBay.get(boxId);
                    if (bay == null) {
                        // defensive: skip if bay not found
                        continue;
                    }

                    // apply allocation to planning map
                    remainingPerBox.put(boxId, avail - take);
                    boxUsageThisLine.put(boxId, boxUsageThisLine.getOrDefault(boxId, 0) + take);

                    // record allocation row
                    AllocationRow ar = new AllocationRow(order.getOrderId(), line.getLineNo(), sku, take, boxId, bay.getAisle(), bay.getBay());
                    lineAllocations.add(ar);

                    remaining -= take;
                    allocatedForLine += take;
                } // end walk boxes

                // determine status based on modePartial
                if (!modePartial) { // strict
                    if (allocatedForLine == line.getQtyRequested()) {
                        // eligible - keep allocations
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), allocatedForLine, LineStatusEnum.ELIGIBLE));
                        allocations.addAll(lineAllocations);
                    } else {
                        // UNDISPATCHABLE - rollback usage for this line (return qty to remainingPerBox)
                        for (Map.Entry<String,Integer> e : boxUsageThisLine.entrySet()) {
                            String boxId = e.getKey();
                            int used = e.getValue();
                            remainingPerBox.put(boxId, remainingPerBox.getOrDefault(boxId, 0) + used);
                        }
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), 0, LineStatusEnum.UNDISPATCHABLE));
                        // do not add allocations
                    }
                } else { // partial mode allowed
                    if (allocatedForLine == 0) {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), 0, LineStatusEnum.UNDISPATCHABLE));
                        // nothing to keep
                    } else if (allocatedForLine < line.getQtyRequested()) {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), allocatedForLine, LineStatusEnum.PARTIAL));
                        allocations.addAll(lineAllocations);
                    } else {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), allocatedForLine, LineStatusEnum.ELIGIBLE));
                        allocations.addAll(lineAllocations);
                    }
                }
            } // end each line
        } // end each order

        return new Result(statuses, allocations);
    }
}

package main.java.pt.ipp.isep.dei.domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for allocating orders to available boxes in the warehouse.
 * <p>
 * Allocation follows FEFO/FIFO principles and can be performed in strict or partial mode:
 * <ul>
 *     <li>Strict mode: lines must be fully allocated to be dispatched (modePartial = false)</li>
 *     <li>Partial mode: lines can be partially allocated if not enough stock (modePartial = true)</li>
 * </ul>
 * </p>
 */
public class OrderAllocator {

    /**
     * Represents the result of an order allocation.
     * Contains the status of each order line and the actual allocation rows.
     */
    public static class Result {
        /** List of statuses for each order line. */
        public final List<LineStatus> lineStatuses;

        /** List of allocation rows describing how quantities were taken from boxes. */
        public final List<AllocationRow> allocationRows;

        /**
         * Constructs a new allocation result.
         *
         * @param lineStatuses   list of line statuses
         * @param allocationRows list of allocation rows
         */
        public Result(List<LineStatus> lineStatuses, List<AllocationRow> allocationRows) {
            this.lineStatuses = lineStatuses;
            this.allocationRows = allocationRows;
        }
    }

    /**
     * Allocates orders to boxes in the warehouse.
     *
     * @param orders      list of orders to allocate
     * @param warehouse   the warehouse containing bays and boxes
     * @param modePartial if true, allows partial line allocations; if false, strict full allocation is required
     * @return a {@link Result} object containing line statuses and allocation rows
     */
    public Result allocateOrders(List<Order> orders, Warehouse warehouse, boolean modePartial) {
        // 1) Prepare mapping of boxId -> Bay and remaining quantities
        Map<String, Bay> boxToBay = new HashMap<>();
        Map<String, Integer> remainingPerBox = new HashMap<>();

        for (Bay bay : warehouse.getBays().values()) {
            for (Box b : bay.getStoredBoxes()) {
                boxToBay.put(b.getBoxId(), bay);
                remainingPerBox.put(b.getBoxId(), b.getQuantity());
            }
        }

        // 2) Prepare FEFO-sorted boxes per SKU
        Comparator<Box> fefoComparator = Comparator
                .comparing((Box b) -> b.getExpiryDate(), Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(Box::getReceivedAt)
                .thenComparing(Box::getBoxId);

        Map<String, List<Box>> skuToBoxesSorted = new HashMap<>();
        for (Bay bay : warehouse.getBays().values()) {
            for (Box b : bay.getStoredBoxes()) {
                skuToBoxesSorted.computeIfAbsent(b.getSku(), k -> new ArrayList<>()).add(b);
            }
        }
        skuToBoxesSorted.values().forEach(list -> list.sort(fefoComparator));

        // 3) Sort orders: priority ASC, dueDate ASC, orderId ASC
        List<Order> sortedOrders = new ArrayList<>(orders);
        sortedOrders.sort(Comparator
                .comparingInt(Order::getPriority)
                .thenComparing(Order::getDueDate)
                .thenComparing(Order::getOrderId));

        List<LineStatus> statuses = new ArrayList<>();
        List<AllocationRow> allocations = new ArrayList<>();

        // 4) Process each order and line
        for (Order order : sortedOrders) {
            List<OrderLine> lines = new ArrayList<>(order.getLines());
            lines.sort(Comparator.comparingInt(OrderLine::getLineNo));

            for (OrderLine line : lines) {
                String sku = line.getSku();
                int remaining = line.getQtyRequested();
                int allocatedForLine = 0;

                List<AllocationRow> lineAllocations = new ArrayList<>();
                Map<String, Integer> boxUsageThisLine = new HashMap<>();

                List<Box> candidateBoxes = skuToBoxesSorted.getOrDefault(sku, Collections.emptyList());

                for (Box box : candidateBoxes) {
                    if (remaining <= 0) break;
                    String boxId = box.getBoxId();
                    Integer avail = remainingPerBox.getOrDefault(boxId, 0);
                    if (avail <= 0) continue;

                    int take = Math.min(remaining, avail);
                    if (take <= 0) continue;

                    Bay bay = boxToBay.get(boxId);
                    if (bay == null) continue;

                    remainingPerBox.put(boxId, avail - take);
                    boxUsageThisLine.put(boxId, boxUsageThisLine.getOrDefault(boxId, 0) + take);

                    AllocationRow ar = new AllocationRow(order.getOrderId(), line.getLineNo(), sku, take, boxId, bay.getAisle(), bay.getBay());
                    lineAllocations.add(ar);

                    remaining -= take;
                    allocatedForLine += take;
                }

                // Determine status
                if (!modePartial) {
                    if (allocatedForLine == line.getQtyRequested()) {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), allocatedForLine, LineStatusEnum.ELIGIBLE));
                        allocations.addAll(lineAllocations);
                    } else {
                        for (Map.Entry<String,Integer> e : boxUsageThisLine.entrySet()) {
                            String boxId = e.getKey();
                            int used = e.getValue();
                            remainingPerBox.put(boxId, remainingPerBox.getOrDefault(boxId, 0) + used);
                        }
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), 0, LineStatusEnum.UNDISPATCHABLE));
                    }
                } else {
                    if (allocatedForLine == 0) {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), 0, LineStatusEnum.UNDISPATCHABLE));
                    } else if (allocatedForLine < line.getQtyRequested()) {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), allocatedForLine, LineStatusEnum.PARTIAL));
                        allocations.addAll(lineAllocations);
                    } else {
                        statuses.add(new LineStatus(order.getOrderId(), line.getLineNo(), sku, line.getQtyRequested(), allocatedForLine, LineStatusEnum.ELIGIBLE));
                        allocations.addAll(lineAllocations);
                    }
                }
            }
        }

        return new Result(statuses, allocations);
    }
}

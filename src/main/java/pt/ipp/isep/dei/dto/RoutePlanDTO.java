package main.java.pt.ipp.isep.dei.dto;

import java.util.ArrayList;
import java.util.List;

public class RoutePlanDTO {
    private final List<Long> freightIds = new ArrayList<>();
    private final List<StopPlanDTO> stops = new ArrayList<>();

    public List<Long> getFreightIds() { return freightIds; }
    public List<StopPlanDTO> getStops() { return stops; }
}

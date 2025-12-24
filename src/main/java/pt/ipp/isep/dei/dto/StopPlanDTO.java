package main.java.pt.ipp.isep.dei.dto;

import java.util.ArrayList;
import java.util.List;

public class StopPlanDTO {
    private final long facilityId;
    private final String facilityName;
    private final List<CargoDTO> toLoad = new ArrayList<>();
    private final List<CargoDTO> toUnload = new ArrayList<>();

    public StopPlanDTO(long facilityId, String facilityName) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
    }

    public long getFacilityId() { return facilityId; }
    public String getFacilityName() { return facilityName; }
    public List<CargoDTO> getToLoad() { return toLoad; }
    public List<CargoDTO> getToUnload() { return toUnload; }
}


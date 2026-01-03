package main.java.pt.ipp.isep.dei.dto;

public record RouteStopDTO(
        Long facilityId,
        String facilityName,
        Integer stopOrder,
        Integer freightsToLoad,
        Integer freightsToUnload
) {
    @Override
    public String toString() {
        return String.format("[%d] %s (Load: %d, Unload: %d)",
                stopOrder, facilityName, freightsToLoad, freightsToUnload);
    }
}

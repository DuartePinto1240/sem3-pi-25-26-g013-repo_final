package main.java.pt.ipp.isep.dei.dto;

public record FacilityDTO(
        Long facilityId,
        String name,
        String location
) {
    @Override
    public String toString() {
        return String.format("[%d] %s (%s)", facilityId, name, location);
    }
}

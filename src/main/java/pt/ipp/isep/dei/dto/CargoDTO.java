package main.java.pt.ipp.isep.dei.dto;

import java.time.LocalDate;

public record CargoDTO(
        long cargoId,
        long wagonId,
        String description,
        String type,
        int quantity,
        double volume,
        double weight,
        LocalDate expiryDate
) {}

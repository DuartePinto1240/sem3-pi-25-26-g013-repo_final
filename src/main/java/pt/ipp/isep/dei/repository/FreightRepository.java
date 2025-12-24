package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.dto.CargoDTO;
import main.java.pt.ipp.isep.dei.dto.PendingFreightDTO;

import java.util.List;

public interface FreightRepository {
    List<PendingFreightDTO> getPendingFreights();
    List<CargoDTO> getCargosForFreight(long freightId);
}

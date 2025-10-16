package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import java.util.List;

public interface LocomotiveRepository {
    Locomotive findById(String id);
    List<Locomotive> findAll();
}
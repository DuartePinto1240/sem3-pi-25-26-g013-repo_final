package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

public interface StationRepository {
    Station findById(String id);
    List<Station> findAll();
}
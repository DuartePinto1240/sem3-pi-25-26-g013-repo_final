package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

/**
 * An interface that defines the contract for a repository of {@link Station} entities.
 * It specifies the standard operations for retrieving station data.
 */
public interface StationRepository {

    /**
     * Finds a station by its unique identifier.
     *
     * @param id The unique ID of the station to find.
     * @return The {@link Station} object if found, otherwise {@code null}.
     */
    Station findById(String id);

    /**
     * Retrieves all stations stored in the repository.
     *
     * @return A {@link List} containing all {@link Station} objects.
     * The list will be empty if the repository contains no stations.
     */
    List<Station> findAll();
}
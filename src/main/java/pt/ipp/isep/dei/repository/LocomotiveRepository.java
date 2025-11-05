package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Locomotive;
import java.util.List;

/**
 * An interface that defines the contract for a repository of {@link Locomotive} entities.
 * It specifies the standard operations for retrieving locomotive data.
 */
public interface LocomotiveRepository {

    /**
     * Finds a locomotive by its unique identifier.
     *
     * @param id The unique ID of the locomotive to find.
     * @return The {@link Locomotive} object if found, otherwise {@code null}.
     */
    Locomotive findById(String id);

    /**
     * Retrieves all locomotives stored in the repository.
     *
     * @return A {@link List} containing all {@link Locomotive} objects.
     * The list will be empty if the repository contains no locomotives.
     */
    List<Locomotive> findAll();
}
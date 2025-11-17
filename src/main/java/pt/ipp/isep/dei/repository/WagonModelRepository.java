package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.WagonModel;

import java.util.List;

/**
 * Repository abstraction for accessing {@link WagonModel} instances.
 */
public interface WagonModelRepository {

    /**
     * Finds a wagon model by its model code (column "Model" in the CSV).
     *
     * @param modelCode model code to search for
     * @return the corresponding {@link WagonModel}, or {@code null} if not found
     */
    WagonModel findByModelCode(String modelCode);

    /**
     * Returns all wagon models known to the repository.
     *
     * @return list of wagon models
     */
    List<WagonModel> findAll();
}

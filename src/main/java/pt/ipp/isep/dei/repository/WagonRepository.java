package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.Wagon;

import java.util.List;

/**
 * Repository abstraction for accessing concrete {@link Wagon} instances.
 */
public interface WagonRepository {

    /**
     * Finds a wagon by its number (column "Number" in the CSV).
     *
     * @param number wagon number
     * @return the corresponding {@link Wagon}, or {@code null} if not found
     */
    Wagon findByNumber(String number);

    /**
     * Returns all wagons known to the repository.
     *
     * @return list of wagons
     */
    List<Wagon> findAll();
}

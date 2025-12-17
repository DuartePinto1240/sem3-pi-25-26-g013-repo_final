package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;
import java.util.List;

/**
 * An interface that defines the contract for a repository of {@link RailwayLine} entities.
 * It specifies the query methods for retrieving railway line data.
 */
public interface RailwayLineRepository {

    /**
     * Finds a direct railway line that connects a specified start and end station.
     * A "direct line" is defined as a {@link RailwayLine} whose first station is the
     * start station and whose last station is the end station.
     *
     * @param start The starting {@link Station} of the line.
     * @param end   The ending {@link Station} of the line.
     * @return The {@link RailwayLine} object if a direct line is found; otherwise, {@code null}.
     */
    RailwayLine findDirectLine(Station start, Station end);


    /**
     * Retrieves all railway lines stored in the repository.
     * Required for graph construction.
     */
    List<RailwayLine> findAll();
}
package main.java.pt.ipp.isep.dei.repository;

import main.java.pt.ipp.isep.dei.domain.RailwayLine;
import main.java.pt.ipp.isep.dei.domain.Station;

public interface RailwayLineRepository {
    RailwayLine findDirectLine(Station start, Station end);
}
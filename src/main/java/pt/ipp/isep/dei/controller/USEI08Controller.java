package main.java.pt.ipp.isep.dei.controller;

import main.java.pt.ipp.isep.dei.domain.KDTreeStations;
import main.java.pt.ipp.isep.dei.domain.TrainStation;

import java.util.List;

/**
 * Controller para a USEI08 - Search by Geographical Area.
 */
public class USEI08Controller {

    private final KDTreeStations kdTreeStations;

    public USEI08Controller(KDTreeStations kdTreeStations) {
        this.kdTreeStations = kdTreeStations;
    }

    public List<TrainStation> searchStationsInArea(double latMin, double latMax,
                                                   double lonMin, double lonMax,
                                                   Boolean isCity,
                                                   Boolean isMainStation,
                                                   String country) {

        return kdTreeStations.rangeSearch(latMin, latMax, lonMin, lonMax, isCity, isMainStation, country);
    }
}
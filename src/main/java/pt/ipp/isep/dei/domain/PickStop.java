package main.java.pt.ipp.isep.dei.domain;

/**
 * Representa uma paragem de picking dentro do armazém.
 * <p>
 * Cada PickStop está associado a uma Allocation específica e a uma Coordinate dentro do armazém.
 * </p>
 */
public class PickStop {

    /** Coordenada do PickStop dentro do armazém (aisle e bay). */
    private Coordinate coordinate;

    /** Allocation associada a esta paragem. */
    private Allocation allocation;

    /**
     * Construtor da paragem de picking.
     *
     * @param coordinate coordenada do PickStop no armazém
     * @param allocation allocation associada a esta paragem
     */
    public PickStop(Coordinate coordinate, Allocation allocation) {
        this.coordinate = coordinate;
        this.allocation = allocation;
    }

    /**
     * Retorna a coordenada desta paragem.
     *
     * @return Coordinate do PickStop
     */
    public Coordinate getCoordinate() {
        return coordinate;
    }

    /**
     * Retorna a allocation associada a esta paragem.
     *
     * @return Allocation do PickStop
     */
    public Allocation getAllocation() {
        return allocation;
    }

    /**
     * Representação textual da paragem de picking.
     *
     * @return string no formato "SKU @ (aisle,bay)"
     */
    @Override
    public String toString() {
        return allocation.getSku() + " @ " + coordinate.toString();
    }
}

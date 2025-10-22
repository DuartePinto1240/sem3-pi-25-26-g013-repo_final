package main.java.pt.ipp.isep.dei.domain;

import java.util.Objects;

/**
 * Representa uma estação ferroviária no sistema.
 * <p>
 * Cada {@code Station} é identificada de forma única por um {@code id} e contém
 * informações básicas como o nome, latitude e longitude. Estas coordenadas podem
 * ser usadas para cálculos geográficos, visualizações ou identificação no mapa.
 * </p>
 *
 * <p>
 * Duas estações são consideradas iguais se tiverem o mesmo identificador
 * ({@code id}), independentemente de outros atributos.
 * </p>
 */
public class Station {

    /** Identificador único da estação (ex: "ST001"). */
    private final String id;

    /** Nome legível da estação (ex: "Lisboa Oriente"). */
    private final String name;

    /** Latitude geográfica da estação (pode ser 0.0 se desconhecida). */
    private final double latitude;

    /** Longitude geográfica da estação (pode ser 0.0 se desconhecida). */
    private final double longitude;

    /**
     * Constrói uma estação sem coordenadas geográficas.
     *
     * @param id identificador único da estação, não pode ser vazio
     * @param name nome da estação, não pode ser vazio
     * @throws IllegalArgumentException se {@code id} ou {@code name} forem nulos ou vazios
     */
    public Station(String id, String name) {
        this(id, name, 0.0, 0.0);
    }

    /**
     * Constrói uma estação com todos os detalhes.
     *
     * @param id identificador único da estação, não pode ser nulo nem vazio
     * @param name nome da estação, não pode ser nulo nem vazio
     * @param latitude latitude da estação (graus decimais)
     * @param longitude longitude da estação (graus decimais)
     * @throws IllegalArgumentException se {@code id} ou {@code name} forem inválidos
     */
    public Station(String id, String name, double latitude, double longitude) {
        if (id == null || id.trim().isEmpty() || name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station ID and Name must not be empty.");
        }
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** @return o identificador único da estação */
    public String getId() {
        return id;
    }

    /** @return o nome da estação */
    public String getName() {
        return name;
    }

    /** @return a latitude geográfica da estação */
    public double getLatitude() {
        return latitude;
    }

    /** @return a longitude geográfica da estação */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Representação textual da estação, contendo o nome e o identificador.
     *
     * @return string no formato "Nome (ID)"
     */
    @Override
    public String toString() {
        return String.format("%s (%s)", name, id);
    }

    /**
     * Duas estações são consideradas iguais se tiverem o mesmo identificador.
     *
     * @param o outro objeto a comparar
     * @return true se as estações tiverem o mesmo {@code id}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Station station = (Station) o;
        return id.equals(station.id);
    }

    /**
     * Calcula o hash code da estação com base no {@code id}.
     *
     * @return valor de hash consistente com {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

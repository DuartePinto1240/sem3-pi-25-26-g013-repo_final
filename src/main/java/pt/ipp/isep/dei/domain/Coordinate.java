package main.java.pt.ipp.isep.dei.domain;

/**
 * Representa uma coordenada dentro de um armazém, composta por um corredor ({@code aisle})
 * e uma posição ({@code bay}) dentro desse corredor.
 *
 * <p>Esta classe é usada para calcular distâncias entre posições de armazenamento no
 * armazém, com base numa métrica específica usada em logística e picking.</p>
 */
public class Coordinate {

    /** Número do corredor (aisle) onde a posição se encontra. */
    private int aisle;

    /** Número da posição (bay) dentro do corredor. */
    private int bay;

    /**
     * Cria uma nova coordenada de armazém.
     *
     * @param aisle número do corredor
     * @param bay   número da posição dentro do corredor
     */
    public Coordinate(int aisle, int bay) {
        this.aisle = aisle;
        this.bay = bay;
    }

    /**
     * Obtém o número do corredor (aisle).
     *
     * @return o número do corredor
     */
    public int getAisle() {
        return aisle;
    }

    /**
     * Obtém o número da posição (bay).
     *
     * @return o número da posição dentro do corredor
     */
    public int getBay() {
        return bay;
    }

    /**
     * Calcula a distância entre esta coordenada e outra, com base num modelo de custo
     * logístico específico do armazém.
     *
     * <p>A regra de cálculo é a seguinte:</p>
     * <ul>
     *   <li>Se ambas as coordenadas estão no mesmo corredor ({@code aisle}),
     *       a distância é a diferença absoluta entre os {@code bay}.</li>
     *   <li>Se estão em corredores diferentes, a distância é calculada como:
     *       {@code this.bay + |this.aisle - other.aisle| * 3 + other.bay}</li>
     * </ul>
     *
     * <p>Este modelo reflete um custo adicional para mudar de corredor,
     * assumindo um custo fixo de 3 unidades por transição de corredor.</p>
     *
     * @param other a outra coordenada a comparar
     * @return a distância entre as duas posições, segundo a métrica definida
     */
    public int distanceTo(Coordinate other) {
        if (this.aisle == other.aisle)
            return Math.abs(this.bay - other.bay);
        else
            return this.bay + Math.abs(this.aisle - other.aisle) * 3 + other.bay;
    }

    /**
     * Retorna uma representação textual da coordenada no formato {@code (aisle,bay)}.
     *
     * @return uma string representando a coordenada
     */
    @Override
    public String toString() {
        return "(" + aisle + "," + bay + ")";
    }
}

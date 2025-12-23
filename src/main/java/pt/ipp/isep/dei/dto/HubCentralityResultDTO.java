package main.java.pt.ipp.isep.dei.dto;

/**
 * DTO holding the computed centrality measures for a station (USEI13).
 * This is NOT domain data; it is the result of a network analysis.
 */
public class HubCentralityResultDTO {

    // Required by the statement
    public final String stid;
    public final String stname;
    public final int degree;
    public final double strength;
    public final double betweenness;
    public final double harmonicCloseness;
    public final double hubScore;

    // Helpful for debugging / proving normalisation in tests
    public final double strengthNorm;
    public final double betweennessNorm;
    public final double harmonicClosenessNorm;

    public HubCentralityResultDTO(
            String stid,
            String stname,
            int degree,
            double strength,
            double betweenness,
            double harmonicCloseness,
            double hubScore,
            double strengthNorm,
            double betweennessNorm,
            double harmonicClosenessNorm
    ) {
        this.stid = stid;
        this.stname = stname;
        this.degree = degree;
        this.strength = strength;
        this.betweenness = betweenness;
        this.harmonicCloseness = harmonicCloseness;
        this.hubScore = hubScore;
        this.strengthNorm = strengthNorm;
        this.betweennessNorm = betweennessNorm;
        this.harmonicClosenessNorm = harmonicClosenessNorm;
    }

    @Override
    public String toString() {
        return String.format(
                "%s | %s | degree=%d | strength=%.2f | betw=%.6f | hc=%.6f | hub=%.6f",
                stid, stname, degree, strength, betweenness, harmonicCloseness, hubScore
        );
    }
}

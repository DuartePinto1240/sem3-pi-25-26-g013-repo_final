package main.java.pt.ipp.isep.dei.service;

import main.java.pt.ipp.isep.dei.dto.LineSegmentDTO;
import main.java.pt.ipp.isep.dei.dto.LocomotiveDTO;
import main.java.pt.ipp.isep.dei.dto.WagonDTO;

import java.util.List;

/**
 * Service for calculating train speeds based on physics
 * Considers: locomotive power, train weight, track limitations
 */
public class SpeedCalculatorService {

    private static final double GRAVITY = 9.81; // m/s²
    private static final double ROLLING_RESISTANCE_COEF = 0.002; // typical for trains

    /**
     * Calculate maximum speed for train on a given segment
     * @return speed in km/h
     */
    public double calculateMaxSpeed(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons,
            LineSegmentDTO segment
    ) {
        double trackLimit = safe(segment.maxSpeed(), 0.0);

        if (locomotives == null || locomotives.isEmpty()) {
            // Without locomotives you cannot compute power/tractive limits correctly
            // but returning 0 here would break scheduling; better to fallback to trackLimit.
            return trackLimit;
        }

        double locoLimit = locomotives.stream()
                .mapToDouble(l -> safe(l.maxSpeed(), trackLimit))
                .min()
                .orElse(trackLimit);

        double totalPowerKw = locomotives.stream()
                .mapToDouble(l -> safe(l.power(), 0.0))
                .sum();

        double totalWeightTonnes = calculateTotalWeight(locomotives, wagons);

        // Rolling resistance force ~ m*g*coef
        double rollingForceN = totalWeightTonnes * 1000.0 * GRAVITY * ROLLING_RESISTANCE_COEF;

        double powerLimitedSpeedKmh;
        if (totalPowerKw <= 0.0 || rollingForceN <= 0.0) {
            // If something is missing, don't kill the schedule; fallback to track/loco limits
            powerLimitedSpeedKmh = Math.min(trackLimit, locoLimit);
        } else {
            // v = P / F (m/s) -> convert to km/h
            powerLimitedSpeedKmh = (totalPowerKw * 1000.0) / rollingForceN * 3.6;
        }

        // electrification constraint
        if (Boolean.FALSE.equals(segment.isElectrified())) {
            boolean allElectric = locomotives.stream().allMatch(l -> "ELECTRIC".equalsIgnoreCase(l.type()));
            if (allElectric) {
                return 0.0; // cannot operate
            }
        }

        return Math.min(Math.min(trackLimit, locoLimit), powerLimitedSpeedKmh);
    }

    /**
     * Calculate operational speed (typically 85% of max for safety/efficiency)
     */
    public double calculateOperationalSpeed(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons,
            LineSegmentDTO segment
    ) {
        double maxSpeed = calculateMaxSpeed(locomotives, wagons, segment);
        return maxSpeed * 0.85;
    }

    /**
     * Calculate acceleration (simplified cap)
     * @return acceleration in m/s²
     */
    public double calculateAcceleration(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons
    ) {
        if (locomotives == null || locomotives.isEmpty()) {
            return 0.12; // safe fallback
        }

        double totalMassKg = calculateTotalWeight(locomotives, wagons) * 1000.0;

        // simplified tractive effort per locomotive
        double tractiveEffortN = locomotives.size() * 350_000.0;

        double a = (totalMassKg <= 0.0) ? 0.12 : (tractiveEffortN / totalMassKg);

        // cap realistic freight accel
        return Math.min(Math.max(a, 0.05), 0.25);
    }

    /**
     * Calculate time to travel a segment (including accel/decel)
     * @return time in minutes
     */
    public double calculateTravelTime(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons,
            LineSegmentDTO segment
    ) {
        double distanceKm = safe(segment.distance(), 0.0);
        double distanceM = distanceKm * 1000.0;

        if (distanceM <= 0.0) return 0.0;

        double vKmh = calculateOperationalSpeed(locomotives, wagons, segment);
        double vMs = vKmh / 3.6;

        // If v is zero (e.g., electric-only on non-electrified), treat as impossible
        if (vMs <= 0.0) {
            throw new RuntimeException("Train cannot operate on segment (speed=0). Check electrification/locomotives.");
        }

        double a = calculateAcceleration(locomotives, wagons);

        // time to reach v: t=v/a
        double tToV = vMs / a;

        // distance during accel: 0.5*a*t^2
        double dAccel = 0.5 * a * tToV * tToV;
        double dDecel = dAccel;

        if (dAccel + dDecel >= distanceM) {
            // triangle profile: never reaches v
            double t = Math.sqrt(distanceM / a);
            return (2.0 * t) / 60.0;
        } else {
            double dCruise = distanceM - dAccel - dDecel;
            double tCruise = dCruise / vMs;
            double totalSec = tToV + tCruise + tToV;
            return totalSec / 60.0;
        }
    }

    /**
     * Total weight (tonnes): loco weight + (tare + payload)
     */
    public double calculateTotalWeight(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons
    ) {
        double locoWeight = 0.0;
        if (locomotives != null) {
            locoWeight = locomotives.stream()
                    .mapToDouble(l -> safe(l.weight(), 0.0))
                    .sum();
        }

        double wagonWeight = 0.0;
        if (wagons != null) {
            wagonWeight = wagons.stream()
                    .mapToDouble(w -> safe(w.tareWeight(), 0.0) + safe(w.payload(), 0.0))
                    .sum();
        }

        return locoWeight + wagonWeight;
    }

    public double calculateTotalLength(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons
    ) {
        double locoLength = 0.0;
        if (locomotives != null) {
            // if you don't have loco length, keep approx
            locoLength = locomotives.size() * 20.0;
        }

        double wagonLength = 0.0;
        if (wagons != null) {
            wagonLength = wagons.stream()
                    .mapToDouble(w -> safe(w.length(), 0.0))
                    .sum();
        }

        return locoLength + wagonLength;
    }

    public boolean canOperate(
            List<LocomotiveDTO> locomotives,
            LineSegmentDTO segment
    ) {
        if (locomotives == null || locomotives.isEmpty()) return false;

        if (Boolean.FALSE.equals(segment.isElectrified())) {
            boolean hasDiesel = locomotives.stream()
                    .anyMatch(l -> "DIESEL".equalsIgnoreCase(l.type()));
            return hasDiesel;
        }

        return true;
    }

    private double safe(Double v, double def) {
        return (v == null || Double.isNaN(v) || Double.isInfinite(v)) ? def : v;
    }
}

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
    private static final double AIR_RESISTANCE_COEF = 0.0035; // simplified

    /**
     * Calculate maximum speed for train on a given segment
     * @return speed in km/h
     */
    public double calculateMaxSpeed(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons,
            LineSegmentDTO segment
    ) {
        // Speed is limited by:
        // 1. Track speed limit
        // 2. Locomotive max speed
        // 3. Train power vs weight ratio

        double trackLimit = segment.maxSpeed();
        double locoLimit = locomotives.stream()
                .mapToDouble(LocomotiveDTO::maxSpeed)
                .min()
                .orElse(trackLimit);

        // Power-limited speed (simplified)
        double totalPower = locomotives.stream()
                .mapToDouble(LocomotiveDTO::power)
                .sum(); // kW

        double totalWeight = calculateTotalWeight(locomotives, wagons);

        // Simplified: speed where power = resistance forces
        // P = F * v => v = P / F
        // F = rolling resistance + air resistance (simplified)
        double rollingForce = totalWeight * 1000 * GRAVITY * ROLLING_RESISTANCE_COEF; // N

        // Power-limited speed (very simplified)
        double powerLimitedSpeed = (totalPower * 1000) / rollingForce * 3.6; // convert m/s to km/h

        // Additional limitations
        if (!segment.isElectrified()) {
            // Check if any locomotive is electric-only
            boolean hasOnlyElectric = locomotives.stream()
                    .allMatch(l -> "ELECTRIC".equals(l.type()));
            if (hasOnlyElectric) {
                return 0.0; // Cannot operate!
            }
        }

        // Return minimum of all limits
        return Math.min(Math.min(trackLimit, locoLimit), powerLimitedSpeed);
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
        return maxSpeed * 0.85; // 85% of max speed
    }

    /**
     * Calculate acceleration
     * @return acceleration in m/s²
     */
    public double calculateAcceleration(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons
    ) {
        double totalPower = locomotives.stream()
                .mapToDouble(LocomotiveDTO::power)
                .sum(); // kW

        double totalWeight = calculateTotalWeight(locomotives, wagons) * 1000; // kg

        // Simplified: assume power can be converted to force at low speeds
        // F = P / v, but at startup v is small, so use tractive effort
        // Typical diesel: ~300 kN, electric: ~400 kN
        double tractiveEffort = locomotives.size() * 350.0 * 1000; // N (simplified)

        // F = m * a => a = F / m
        double acceleration = tractiveEffort / totalWeight;

        // Realistic limits for freight trains: 0.1 - 0.3 m/s²
        return Math.min(acceleration, 0.25);
    }

    /**
     * Calculate time to travel a segment (including acceleration)
     * @param segment distance in kilometers
     * @return time in minutes
     */
    public double calculateTravelTime(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons,
            LineSegmentDTO segment
    ) {
        double distanceKm = segment.distance();
        double distanceM = distanceKm * 1000;

        double maxSpeed = calculateOperationalSpeed(locomotives, wagons, segment);
        double maxSpeedMs = maxSpeed / 3.6; // convert km/h to m/s

        double acceleration = calculateAcceleration(locomotives, wagons);

        // Time to reach max speed: t = v / a
        double timeToMaxSpeed = maxSpeedMs / acceleration; // seconds

        // Distance covered during acceleration: d = 0.5 * a * t²
        double accelDistance = 0.5 * acceleration * timeToMaxSpeed * timeToMaxSpeed; // meters

        // Distance covered during deceleration (same as acceleration)
        double decelDistance = accelDistance;

        // Check if we reach max speed at all
        if ((accelDistance + decelDistance) >= distanceM) {
            // Triangle profile (never reach max speed)
            // d = a * t² => t = sqrt(d / a)
            double totalTime = Math.sqrt(distanceM / acceleration); // seconds
            return totalTime / 60.0; // convert to minutes
        } else {
            // Trapezoid profile
            double constantSpeedDistance = distanceM - accelDistance - decelDistance;
            double constantSpeedTime = constantSpeedDistance / maxSpeedMs; // seconds

            double totalTime = timeToMaxSpeed + constantSpeedTime + timeToMaxSpeed; // seconds
            return totalTime / 60.0; // convert to minutes
        }
    }

    /**
     * Calculate total weight of train
     * @return weight in tonnes
     */
    public double calculateTotalWeight(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons
    ) {
        double locoWeight = locomotives.stream()
                .mapToDouble(LocomotiveDTO::weight)
                .sum();

        double wagonWeight = wagons.stream()
                .mapToDouble(w -> w.tareWeight() + w.payload())
                .sum();

        return locoWeight + wagonWeight;
    }

    /**
     * Calculate total length of train
     * @return length in meters
     */
    public double calculateTotalLength(
            List<LocomotiveDTO> locomotives,
            List<WagonDTO> wagons
    ) {
        double locoLength = locomotives.stream()
                .mapToDouble(l -> 20.0) // approximate locomotive length
                .sum();

        double wagonLength = wagons.stream()
                .mapToDouble(WagonDTO::length)
                .sum();

        return locoLength + wagonLength;
    }

    /**
     * Check if train can operate on segment
     */
    public boolean canOperate(
            List<LocomotiveDTO> locomotives,
            LineSegmentDTO segment
    ) {
        // Check electrification compatibility
        if (!segment.isElectrified()) {
            // Need at least one diesel locomotive
            boolean hasDiesel = locomotives.stream()
                    .anyMatch(l -> "DIESEL".equals(l.type()));
            if (!hasDiesel) {
                return false;
            }
        }

        // Check gauge compatibility (simplified - assume all match)
        // In reality, you'd check locomotive gauge vs segment gauge

        return true;
    }
}
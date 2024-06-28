package com.waterloorocketry.airbrakeplugin.airbrake;

import java.util.List;

/**
 * Airbrakes behavior interface
 * <p>
 * The implementation this object determines the effect of the airbrakes
 * on the rocket based on the extension state.
 */
public interface Airbrakes {
    /**
     * Returns the coefficient of drag of the rocket based on the airbrakes extension amount
     * @param extension Airbrakes extension value from 0 to 1 inclusive
     * @param velocity Upwards velocity of the rocket in m/s
     * @param altitude Altitude of the rocket in m
     * @return The CD value
     */
    double calculateDragForce(double extension, double velocity, double altitude);

    /**
     * Returns the effective extension of the airbrakes based on the past calculated values
     * @param timestamps Timestamps in seconds of extension amounts, in chronological order
     * @param extensions The extension amounts corresponding to the timestamps
     * @param currentTimestamp Timestamp to calculate effective extension at
     * @return Effective extension based on information
     */
    double getAppliedAirbrakesExtension(List<Double> timestamps, List<Double> extensions, double currentTimestamp);
}

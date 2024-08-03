package com.waterloorocketry.airbrakeplugin.airbrake;

import com.waterloorocketry.airbrakeplugin.simulated.SimulatedDragForceInterpolator;

import java.util.Collections;
import java.util.List;

/**
 * Airbrakes using CFD simulated values
 */
public class SimulatedAirbrakes implements Airbrakes {
    private final SimulatedDragForceInterpolator interp = new SimulatedDragForceInterpolator();

    @Override
    public double calculateDragForce(double extension, double velocity, double altitude) {
        return interp.compute(new SimulatedDragForceInterpolator.Data(extension, velocity, altitude));
    }

    @Override
    public double getAppliedAirbrakesExtension(List<Double> timestamps, List<Double> extensions, double currentTimestamp) {
        int idx = Collections.binarySearch(timestamps, currentTimestamp - 0.5);
        if (idx >= 0) {
            return extensions.get(idx);
        } else if (idx == -1) {
            return 0.0;
        } else {
            return extensions.get(-(idx + 1));
        }
    }
}

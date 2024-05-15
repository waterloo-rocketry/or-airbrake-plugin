package com.waterloorocketry.airbrakeplugin.airbrake;

import com.waterloorocketry.airbrakeplugin.simulated.SimulatedDragForceInterpolator;

/**
 * Airbrakes using CFD simulated values
 */
public class SimulatedAirbrakes implements Airbrakes {
    private final SimulatedDragForceInterpolator interp = new SimulatedDragForceInterpolator();

    @Override
    public double calculateDragForce(double extension, double velocity, double altitude) {
        return interp.compute(new SimulatedDragForceInterpolator.Data(extension, velocity, altitude));
    }
}

package com.waterloorocketry.airbrakeplugin.controller;

import net.sf.openrocket.simulation.SimulationStatus;

/**
 * Control airbrake extension during simulation
 */
public interface Controller {

    /**
     * Calculate the target airbrake extension given current flight conditions
     * @return Target airbrake extension percent [0, 1]
     */
    public double calculateTargetExt(SimulationStatus status);
}

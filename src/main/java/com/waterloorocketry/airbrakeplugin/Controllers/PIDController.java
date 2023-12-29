package com.waterloorocketry.airbrakeplugin.Controllers;

import net.sf.openrocket.simulation.SimulationStatus;

public class PIDController implements Controller {
    @Override
    public double calculateTargetExt(SimulationStatus status) {
        // % airbrake extension [0-1]
        double output = 1;

        // do pid stuff

        return output;
    }
}

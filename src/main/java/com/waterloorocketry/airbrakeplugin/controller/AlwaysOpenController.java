package com.waterloorocketry.airbrakeplugin.controller;

import net.sf.openrocket.simulation.SimulationStatus;

public class AlwaysOpenController implements Controller {
    @Override
    public double calculateTargetExt(SimulationStatus status) {
        return 1;
    }
}

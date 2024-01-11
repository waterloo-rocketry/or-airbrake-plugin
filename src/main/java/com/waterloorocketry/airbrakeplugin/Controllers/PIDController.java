package com.waterloorocketry.airbrakeplugin.Controllers;

import net.sf.openrocket.simulation.SimulationStatus;

public class PIDController implements Controller {
    private static final double Kp = 0.001;
    private static final double Ti = 1.0;
    private static final double Td = 1.0;


    private final double targetAltitude;

    public PIDController(double targetAltitude) {
        this.targetAltitude = targetAltitude;
    }

    private static class LastState {
        private final double error;
        private final double time;

        private LastState(double error, double time) {
            this.error = error;
            this.time = time;
        }
    }

    private LastState lastState;
    private double integral = 0.0;

    @Override
    public double calculateTargetExt(SimulationStatus status) {
        double altitude = status.getRocketPosition().y;
        double error = targetAltitude - altitude;
        double time = status.getSimulationTime();
        if (lastState != null) {
            integral += (time - lastState.time) * (error + lastState.error) * 0.5;
        }
        lastState = new LastState(altitude, time);

        double derivative = -status.getRocketVelocity().y;

        double ans = Kp * (error + integral / Ti + Td * derivative);

        System.out.println("ans " + ans);

        // % airbrake extension [0-1]
        return Math.max(Math.min(ans, 1.0), 0.0);
    }
}

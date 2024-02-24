package com.waterloorocketry.airbrakeplugin.controller;


public class PIDController implements Controller {
    private static final double Kp = 0.00005;
    private static final double Ti = 2;
    private static final double Td = 0.5;


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
    public double calculateTargetExt(double[] flightData, double time) {
        double altitude = flightData[1];
        double error = targetAltitude - altitude;
        if (lastState != null) {
            integral += (time - lastState.time) * (error + lastState.error) * 0.5;
        }
        lastState = new LastState(altitude, time);

        double derivative = -flightData[4];

        double ans = Kp * (error + integral / Ti + Td * derivative);

        System.out.println("extension " + ans);

        // % airbrake extension [0-1]
        return Math.max(Math.min(ans, 1.0), 0.0);
    }
}

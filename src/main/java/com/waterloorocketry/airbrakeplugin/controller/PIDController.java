package com.waterloorocketry.airbrakeplugin.controller;

public class PIDController implements Controller {
    private static final double Kp = 0.00005;
    private static final double Ti = 0.1;
    private static final double Td = 0.05;
    private static final double ROCKET_BURNOUT_MASS = 39.564; //kg
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
    public double calculateTargetExt(double[] flightData, double time, double extension) {
        double altitude = TrajectoryPrediction.get_max_altitude(flightData[5], flightData[2], extension, ROCKET_BURNOUT_MASS); //z displacement, z velocity, ...; +Z is "up" in OR
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

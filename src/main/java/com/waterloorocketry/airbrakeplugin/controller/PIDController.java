package com.waterloorocketry.airbrakeplugin.controller;

public class PIDController implements Controller {
    private static final double ROCKET_BURNOUT_MASS = 39.564; //kg
    private final double Kp;
    private final double Ki;
    private final double Kd;
    private final double targetAltitude;

    public PIDController(double targetAltitude, double Kp, double Ki, double Kd) {
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
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
    public double calculateTargetExt(RocketState rocketState, double time, double extension) {
        double altitude = TrajectoryPrediction.get_max_altitude(rocketState.velocityZ, rocketState.positionZ, extension, ROCKET_BURNOUT_MASS); //z displacement, z velocity, ...; +Z is "up" in OR
        double error = targetAltitude - altitude;
        if (lastState != null) {
            integral += (time - lastState.time) * (error + lastState.error) * 0.5;
        }
        lastState = new LastState(altitude, time);

        double derivative = -rocketState.velocityY;

        // PLACEHOLDER CODE TO RETURN A VALID DOUBLE. not actual pid
        double output = Kp + Ki + Kd;

        System.out.println("extension " + output);

        // % airbrake extension [0-1]
        return Math.max(Math.min(output, 1.0), 0.0);
    }
}

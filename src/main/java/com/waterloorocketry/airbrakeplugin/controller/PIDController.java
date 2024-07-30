package com.waterloorocketry.airbrakeplugin.controller;


import com.waterloorocketry.airbrakeplugin.jni.ProcessorCalculations;

public class PIDController implements Controller {
    private final ProcessorCalculations.ControllerState impl = new ProcessorCalculations.ControllerState();

    private static final double ROCKET_BURNOUT_MASS = 39.609; //kg
    private final double targetAltitude;

    public PIDController(double targetAltitude, double Kp, double Ki, double Kd) {
        this.targetAltitude = targetAltitude;
    }

    @Override
    public double calculateTargetExt(RocketState rocketState, double time, double extension) {
        double vX = Math.sqrt(rocketState.velocityX * rocketState.velocityX + rocketState.velocityY * rocketState.velocityY);
        double predicted = ProcessorCalculations.getMaxAltitude((float) rocketState.velocityZ, (float) vX, (float) rocketState.positionZ, 0.5F, (float) ROCKET_BURNOUT_MASS); //z displacement, z velocity, ...; +Z is "up" in OR
        float control = impl.updateController((float) (time * 1000), (float) (targetAltitude - predicted));
        return Math.max(0, Math.min(1, 0.5 - control));
    }
}

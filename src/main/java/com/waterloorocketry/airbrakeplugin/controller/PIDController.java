package com.waterloorocketry.airbrakeplugin.controller;


import com.waterloorocketry.airbrakeplugin.jni.ProcessorCalculations;

public class PIDController implements Controller {
    private final ProcessorCalculations.ControllerState impl = new ProcessorCalculations.ControllerState();

    private final float targetAltitude;
    private final float Kp;
    private final float Ki;
    private final float Kd;

    public PIDController(float targetAltitude, float Kp, float Ki, float Kd) {
        this.targetAltitude = targetAltitude;
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
    }

    @Override
    public double calculateTargetExt(RocketState rocketState, double time, double extension) {
        double vX = Math.sqrt(rocketState.velocityX * rocketState.velocityX + rocketState.velocityY * rocketState.velocityY);
        float predicted = ProcessorCalculations.getMaxAltitude((float) rocketState.velocityZ, (float) vX, (float) rocketState.positionZ); //z displacement, z velocity, ...; +Z is "up" in OR
        return impl.updateController(Kp, Ki, Kd, 100000, (float) (time * 1000), predicted, targetAltitude);
    }
}

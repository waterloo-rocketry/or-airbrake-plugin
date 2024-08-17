package com.waterloorocketry.airbrakeplugin.controller;


import com.waterloorocketry.airbrakeplugin.jni.ProcessorCalculations;

public class PIDController implements Controller {
    private final ProcessorCalculations.ControllerState impl = new ProcessorCalculations.ControllerState();

    private final float targetAltitude;
    private final float Kp;
    private final float Ki;
    private final float Kd;
    private final float iSatmax;
    private final float extRef;

    public PIDController(float targetAltitude, float Kp, float Ki, float Kd, float iSatmax, float extRef) {
        this.targetAltitude = targetAltitude;
        this.Kp = Kp;
        this.Ki = Ki;
        this.Kd = Kd;
        this.iSatmax = iSatmax;
        this.extRef = extRef;
    }

    @Override
    public double calculateTargetExt(RocketState rocketState, double time, double extension) {
        double vX = Math.sqrt(rocketState.velocityX * rocketState.velocityX + rocketState.velocityY * rocketState.velocityY);
        float predicted = ProcessorCalculations.getMaxAltitude(extRef, (float) rocketState.velocityZ, (float) vX, (float) rocketState.positionZ); //z displacement, z velocity, ...; +Z is "up" in OR
        return impl.updateController(Kp, Ki, Kd, iSatmax, extRef, (float) (time * 1000), predicted, targetAltitude);
    }
}

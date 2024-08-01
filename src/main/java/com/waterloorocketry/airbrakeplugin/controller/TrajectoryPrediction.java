package com.waterloorocketry.airbrakeplugin.controller;

import com.waterloorocketry.airbrakeplugin.jni.ProcessorCalculations;

public class TrajectoryPrediction {
    /** @return max apogee from proc firmware's trajpred lib
     * @param rocketState rocket state
     * */
    public static double get_max_altitude(Controller.RocketState rocketState) {
        double vX = Math.sqrt(rocketState.velocityX * rocketState.velocityX + rocketState.velocityY * rocketState.velocityY);
        return ProcessorCalculations.getMaxAltitude((float) rocketState.velocityZ, (float) vX, (float) rocketState.positionZ); //z displacement, z velocity, ...; +Z is "up" in OR
    }
}


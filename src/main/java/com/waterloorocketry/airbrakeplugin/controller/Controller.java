package com.waterloorocketry.airbrakeplugin.controller;

/**
 * Control airbrake extension during simulation
 */
public interface Controller {

    /**
     * Calculate the target airbrake extension given current flight conditions
     * @param flightData XYZ values of position and velocity and XYZW values of orientation
     * @param timestamp Timestamp of the controller calculation
     * @return Target airbrake extension percent [0, 1]
     */
    double calculateTargetExt(double[] flightData, double timestamp, double extension);
}

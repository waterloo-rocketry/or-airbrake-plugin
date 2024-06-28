package com.waterloorocketry.airbrakeplugin.controller;

import net.sf.openrocket.simulation.SimulationStatus;

/**
 * Control airbrake extension during simulation
 */
public interface Controller {

    /**
     * Calculate the target airbrake extension given current flight conditions
     * @param rocketState XYZ values of position and velocity and XYZW values of orientation
     * @param timestamp Timestamp of the controller calculation
     * @return Target airbrake extension percent [0, 1]
     */
    double calculateTargetExt(RocketState rocketState, double timestamp, double extension);

    /**
     * XYZ of position, XYZ of velocity, XYZW of rotation quaternion
     */
    class RocketState {
        public double positionX;
        public double positionY;
        public double positionZ;
        public double velocityX;
        public double velocityY;
        public double velocityZ;
        public double orientationX;
        public double orientationY;
        public double orientationZ;
        public double orientationW;

        /**
         * Constructs the rocket state from OpenRocket
         * @param status OpenRocket simulation status
         */
        public RocketState(SimulationStatus status) {
            this.positionX = status.getRocketPosition().x;
            this.positionY = status.getRocketPosition().y;
            this.positionZ = status.getRocketPosition().z;
            this.velocityX = status.getRocketVelocity().x;
            this.velocityY = status.getRocketVelocity().y;
            this.velocityZ = status.getRocketVelocity().z;
            this.orientationX = status.getRocketOrientationQuaternion().getX();
            this.orientationY = status.getRocketOrientationQuaternion().getY();
            this.orientationZ = status.getRocketOrientationQuaternion().getZ();
            this.orientationW = status.getRocketOrientationQuaternion().getW();
        }
    }
}

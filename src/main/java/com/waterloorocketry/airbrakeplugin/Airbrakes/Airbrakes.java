package com.waterloorocketry.airbrakeplugin.Airbrakes;

import com.waterloorocketry.airbrakeplugin.Controllers.Controller;

public class Airbrakes {
    private final double cfdDataConstant = 50;

    public Airbrakes() {
    }

    /**
     * Calculate and return the drag force of the rocket given the current airbrake extension and flight conditions.
     * @param velocity
     * @param airbrakeExt
     * @return Rocket drag force
     */
    public double calculateDragForce(Controller controller, double velocity, double airbrakeExt) {
        return airbrakeExt * cfdDataConstant;
    }
}

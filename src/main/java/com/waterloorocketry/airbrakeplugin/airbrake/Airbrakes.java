package com.waterloorocketry.airbrakeplugin.airbrake;

import com.waterloorocketry.airbrakeplugin.controller.Controller;

public class Airbrakes {
    public Airbrakes() {
    }

    /**
     * Calculate and return the coefficient of drag of the rocket given the current airbrake extension and flight conditions.
     * @param velocity
     * @param airbrakeExt [0, 1]
     * @return Rocket drag force
     */
    public double calculateCD(Controller controller, double velocity, double airbrakeExt) {
        return 0.5 + airbrakeExt;
    }
}

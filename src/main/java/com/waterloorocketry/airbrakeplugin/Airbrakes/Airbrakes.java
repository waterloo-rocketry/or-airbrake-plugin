package com.waterloorocketry.airbrakeplugin.Airbrakes;

import com.waterloorocketry.airbrakeplugin.Controllers.Controller;

public class Airbrakes {
    public Airbrakes() {
    }

    /**
     * Calculate and return the coefficient of drag of the rocket given the current airbrake extension and flight conditions.
     * @param velocity
     * @param airbrakeExt
     * @return Rocket drag force
     */
    public double calculateCD(Controller controller, double velocity, double airbrakeExt) {
        return 0.5 + airbrakeExt;
    }
}

package com.waterloorocketry.airbrakeplugin.airbrake;

/**
 * Airbrakes behavior interface
 *
 * The implementation this object determines the effect of the airbrakes
 * on the rocket based on the extension state.
 */
public interface Airbrakes {
    /**
     * Returns the coefficient of drag of the rocket based on the airbrakes extension amount
     * @param ext Extension from 0 to 1
     * @return The CD value
     */
    double calculateCD(double ext);
}

package com.waterloorocketry.airbrakeplugin.controller;

import com.waterloorocketry.airbrakeplugin.simulated.AirDensity;
import com.waterloorocketry.airbrakeplugin.simulated.SimulatedDragForceInterpolator;

public class TrajectoryPrediction {
    private static final double GRAV_AT_SEA_LVL = 9.80665;
    private static final double EARTH_MEAN_RADIUS = 6371009;
    private static final double AIRBRAKES_MAX_AREA = 0.004993538; //m^2
    private static final double ROCKET_BASE_AREA = 0.0182412538; //m^2
    /**
     * Floating point inaccuracy tolerance
     */
    private static final double TOL = 0.00001;

    private static final SimulatedDragForceInterpolator interp = new SimulatedDragForceInterpolator();

    private static class RK4Integrals {
        public double vel;
        public double alt;
    }

    /**
     *  @return acceleration (m/s^2)
     */
    private static double velocity_derivative(double force, double mass) {
        return force/mass;
    }

    /**
     * rk4 method to integrate altitude from velocity, and integrate velocity from acceleration (force/mass)
     * @param h time step
     * @param force sum of forces acting on rocket at given time (N)
     * @param mass of rocket (kg)
     * @param integrals, altitude (m) and velocity (m/s)
     * @return updated altitude and velocity integrals after one rk4 step
     */
    private static RK4Integrals rk4(double h, double force, double mass, RK4Integrals integrals) {
        double ka1 = h * integrals.alt;
        double kv1 = h * velocity_derivative(force, mass);

        double ka2 = h * (integrals.alt + h*ka1/2);
        double kv2 = h * velocity_derivative(force + h*kv1/2, mass);

        double ka3 = h * (integrals.alt + h*ka2/2);
        double kv3 = h * velocity_derivative(force + h*kv2/2, mass);

        double ka4 = h * (integrals.alt + h*ka3);
        double kv4 = h * velocity_derivative(force + h*kv3, mass);

        RK4Integrals updatedIntegrals = new RK4Integrals();
        updatedIntegrals.vel = (integrals.vel + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        updatedIntegrals.alt = (integrals.alt + (kv1 + 2*kv2 + 2*kv3 + kv4)/6);

        return updatedIntegrals;
    }

    /**
     * @param altitude (m)
     * @return acceleration due to gravity (m/s^2)
     */
    private static double gravitational_acceleration(double altitude) {
        return GRAV_AT_SEA_LVL * Math.pow(EARTH_MEAN_RADIUS / ( EARTH_MEAN_RADIUS + altitude), 2);
    }

    /**
     * Does not take into account fins
     * @param extension extension of the airbrakes (0-1)
     * @return rocket's cross-sectional area from airbrake extension
     */
    private static double rocket_area(double extension) {
        return (AIRBRAKES_MAX_AREA * extension) + ROCKET_BASE_AREA;
    }



    /** TODO: rather hack way of overriding for OR to do its step. This should be replaced ideally by overriding OR's drag calculation, rather than just the CD parameter
     * @return Cd value corresponding to interpolated drag force
     */
    public static double interpolate_cd(double extension, double velocity, double altitude){
        double drag_force = interp.compute(new SimulatedDragForceInterpolator.Data(extension, velocity, altitude));
        return 2 * drag_force / (AirDensity.getAirDensityAtAltitude(altitude) * rocket_area(extension) * (velocity * velocity)); //Cd
    }

    /** @return max apogee
     * @param velocity vertical velocity (m/s)
     * @param altitude (m)
     * @param airbrake_ext extension of airbrakes, 0-1
     * @param mass (kg)*/
    public static double get_max_altitude(double velocity, double altitude, double airbrake_ext, double mass) {

        double h = 0.05; // interval of change for rk4
        double prevAlt = 0.0; // variable to store previous altitude

        RK4Integrals states = new RK4Integrals();
        states.alt = altitude;
        states.vel = velocity;

        while (states.alt >= prevAlt) {
            // update forces of drag and gravity from new altitude
            double Fg = -gravitational_acceleration(states.alt) * mass; // force of gravity (N)
            double Fd = -interp.compute(new SimulatedDragForceInterpolator.Data(airbrake_ext, states.vel, states.alt)); // force of drag (N)

            // to check if altitude is decreasing to exit the loop
            prevAlt = states.alt;

            // update velocity and altitude
            states = rk4(h, Fg+Fd, mass, states);

            // System.out.println("pred alt: " + states.alt + "m");
        }

        return altitude;
    }
}


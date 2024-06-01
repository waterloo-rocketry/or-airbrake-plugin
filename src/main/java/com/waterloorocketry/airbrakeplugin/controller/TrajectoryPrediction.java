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

    private static class RK4State {
        public double velX;
        public double velY;
        public double alt;
    }

    private static class Forces {
        public double Fx;
        public double Fy;
    }


    /**
     *  @return acceleration (m/s^2)
     */
    private static double acceleration(double force, double mass) {
        return force/mass;
    }

    /**
     * @param extension extension of airbrakes, 0-1
     * @param mass of rocket (kg)
     * @param velX, velocity in X direction (m/s)
     * @param velY, velocity in Y direction (m/s)
     * @param alt, altitude (m)
     * @return forces acting on rocket in the X and Y directions (N)
     */
    static Forces get_forces(double extension, double mass, double velX, double velY, double alt){
        Forces forces = new Forces();
        double angle = Math.atan(velX / velY);
        double Fd = -interp.compute(new SimulatedDragForceInterpolator.Data(extension, Math.sqrt(velY*velY + velX*velX), alt)); // force of drag (N)
        double Fg = -gravitational_acceleration(alt) * mass; // force of gravity (N)
        forces.Fy = Fd * Math.cos(angle) + Fg;
        forces.Fx = Fd * Math.sin(angle);
        return forces;
    }

    /**
     * rk4 method to integrate altitude from velocity, and integrate velocity from acceleration (force/mass)
     * @param h time step
     * @param extension extension of airbrakes, 0-1
     * @param mass of rocket (kg)
     * @param state, including altitude (m) and velocity in X and Y directions (m/s)
     * @return updated altitude and velocity integrals after one rk4 step
     */
    static RK4State rk4(double h, double mass, double extension, RK4State state) {
        Forces forces;

        forces = get_forces(extension, mass, state.velX, state.velY, state.alt);
        double ka1 = h * state.velY;
        double kvY1 = h * acceleration(forces.Fy, mass);
        double kvX1 = h * acceleration(forces.Fx, mass);

        forces = get_forces(extension, mass, state.velX + kvX1/2, state.velY + kvY1/2, state.alt + ka1/2);
        double ka2 = h * (state.velY + h*kvY1/2);
        double kvY2 = h * acceleration(forces.Fy, mass);
        double kvX2 = h * acceleration(forces.Fx, mass);

        forces = get_forces(extension, mass, state.velX + kvX2/2, state.velY + kvY2/2, state.alt + ka2/2);
        double ka3 = h * (state.velY + h*kvY2/2);
        double kvY3 = h * acceleration(forces.Fy, mass);
        double kvX3 = h * acceleration(forces.Fx, mass);

        forces = get_forces(extension, mass, state.velX + kvX3, state.velY + kvY3, state.alt + ka3);
        double ka4 = h * (state.velY + h*kvY3);
        double kvY4 = h * acceleration(forces.Fy, mass);
        double kvX4 = h * acceleration(forces.Fx, mass);

        RK4State updatedState= new RK4State();
        updatedState.alt = (state.alt + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        updatedState.velY = (state.velY + (kvY1 + 2*kvY2 + 2*kvY3 + kvY4)/6);
        updatedState.velX = (state.velX + (kvX1 + 2*kvX2 + 2*kvX3 + kvX4)/6);

        return updatedState;
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
     * @param velocityY vertical velocity (m/s)
     * @param velocityX horizontal velocity (m/s)
     * @param altitude (m)
     * @param airbrake_ext extension of airbrakes, 0-1
     * @param mass (kg)*/
    public static double get_max_altitude(double velocityY, double velocityX, double altitude, double airbrake_ext, double mass) {

        double h = 0.05; // interval of change for rk4
        double prevAlt = 0.0; // variable to store previous altitude

        RK4State states = new RK4State();
        states.alt = altitude;
        states.velY = velocityY;
        states.velX = velocityX;

        while (states.alt >= prevAlt) {
            // update forces of drag and gravity from new altitude
            //double Fg = -gravitational_acceleration(states.alt) * mass; // force of gravity (N)
            //double Fd = -interp.compute(new SimulatedDragForceInterpolator.Data(airbrake_ext, states.vel, states.alt)); // force of drag (N)

            // to check if altitude is decreasing to exit the loop
            prevAlt = states.alt;

            // update velocity and altitude
            states = rk4(h, mass, airbrake_ext, states);

            // System.out.println("pred alt: " + states.alt + "m");
        }

        return states.alt;
    }
}


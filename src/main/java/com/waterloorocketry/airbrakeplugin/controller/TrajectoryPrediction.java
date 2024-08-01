package com.waterloorocketry.airbrakeplugin.controller;

import com.waterloorocketry.airbrakeplugin.jni.ProcessorCalculations;
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
        public double velZ;
        public double alt;
    }

    private static class Forces {
        public double Fx;
        public double Fz;
    }


    /**
     *  @return acceleration (m/s^2)
     */
    private static double acceleration(double force, double mass) {
        return force/mass;
    }

    /**
     * @param extension of airbrakes, 0-1
     * @param mass of rocket (kg)
     * @param velX, velocity in X direction (m/s)
     * @param velZ, velocity in Y direction (m/s)
     * @param alt, altitude (m)
     * @return forces acting on rocket in the X and Y directions (N)
     */
    static Forces get_forces(double extension, double mass, double velX, double velZ, double alt){
        Forces forces = new Forces();
        double velT = Math.sqrt(velZ*velZ + velX*velX);
        double Fd = -ProcessorCalculations.interpolateDrag((float) extension, (float) velT, (float) alt);
//        double Fd = -interp.compute(new SimulatedDragForceInterpolator.Data(extension, velT, alt)); // force of drag (N)
        double Fg = -gravitational_acceleration(alt) * mass; // force of gravity (N)
        forces.Fz = Fd * (velZ/velT) + Fg;
        forces.Fx = Fd * (velX/velT);
        return forces;
    }

    /**
     * rk4 method to integrate altitude from velocity, and integrate velocity from acceleration (force/mass)
     * @param h time step
     * @param extension of airbrakes, 0-1
     * @param mass of rocket (kg)
     * @param state, including altitude (m) and velocity in X and Y directions (m/s)
     * @return updated altitude and velocity integrals after one rk4 step
     */
    static RK4State rk4(double h, double mass, double extension, RK4State state) {
        Forces forces;

        forces = get_forces(extension, mass, state.velX, state.velZ, state.alt);
        double ka1 = h * state.velZ;
        double kvZ1 = h * acceleration(forces.Fz, mass);
        double kvX1 = h * acceleration(forces.Fx, mass);

        forces = get_forces(extension, mass, state.velX + kvX1/2, state.velZ + kvZ1/2, state.alt + ka1/2);
        double ka2 = h * (state.velZ + h*kvZ1/2);
        double kvZ2 = h * acceleration(forces.Fz, mass);
        double kvX2 = h * acceleration(forces.Fx, mass);

        forces = get_forces(extension, mass, state.velX + kvX2/2, state.velZ + kvZ2/2, state.alt + ka2/2);
        double ka3 = h * (state.velZ + h*kvZ2/2);
        double kvZ3 = h * acceleration(forces.Fz, mass);
        double kvX3 = h * acceleration(forces.Fx, mass);

        forces = get_forces(extension, mass, state.velX + kvX3, state.velZ + kvZ3, state.alt + ka3);
        double ka4 = h * (state.velZ + h*kvZ3);
        double kvZ4 = h * acceleration(forces.Fz, mass);
        double kvX4 = h * acceleration(forces.Fx, mass);

        RK4State updatedState= new RK4State();
        updatedState.alt = (state.alt + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        updatedState.velZ = (state.velZ + (kvZ1 + 2*kvZ2 + 2*kvZ3 + kvZ4)/6);
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
     * @param extension of the airbrakes (0-1)
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

    /** @return max apogee from proc firmware's trajpred lib
     * @param rocketState rocket state
     * */
    public static double get_max_altitude(Controller.RocketState rocketState) {
        double vX = Math.sqrt(rocketState.velocityX * rocketState.velocityX + rocketState.velocityY * rocketState.velocityY);
        return ProcessorCalculations.getMaxAltitude((float) rocketState.velocityZ, (float) vX, (float) rocketState.positionZ); //z displacement, z velocity, ...; +Z is "up" in OR
    }
}


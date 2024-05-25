package com.waterloorocketry.airbrakeplugin.controller;

public class TrajectoryPrediction {
    private static final double GRAV_AT_SEA_LVL = 9.80665;
    private static final double EARTH_MEAN_RADIUS = 6371009;
    private static final double AIRBRAKES_MAX_AREA = 0.004993538; //m^2
    private static final double ROCKET_BASE_AREA = 0.0182412538; //m^2
    private static final double SIM_ALTITUDE = 1000; //All drag sims conducted at 1000m above sea level
    /**
     * Floating point inaccuracy tolerance
     */
    private static final double TOL = 0.00001;

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
     * ka is for altitude and kv is for velocity
     */
    private static RK4Integrals rk4(double h, double force, double mass, RK4Integrals integrals) {
        double ka1 = h * integrals.vel;
        double kv1 = h * velocity_derivative(force, mass);

        double ka2 = h * (integrals.vel + h*ka1/2);
        double kv2 = h * velocity_derivative(force + h*kv1/2, mass);

        double ka3 = h * (integrals.vel + h*ka2/2);
        double kv3 = h * velocity_derivative(force + h*kv2/2, mass);

        double ka4 = h * (integrals.vel + h*ka3);
        double kv4 = h * velocity_derivative(force + h*kv3, mass);

        RK4Integrals updatedIntegrals = new RK4Integrals();
        updatedIntegrals.alt = (integrals.alt + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        updatedIntegrals.vel = (integrals.vel + (kv1 + 2*kv2 + 2*kv3 + kv4)/6);

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
     * @param altitude (m)
     * @return air density (kg/m^3)
     * Same method used for PyAnsys simulations, verified to be correct
     */
    private static double air_density(double altitude) {
        // Based on US Standard Atmosphere 1976
        double temperature, pressure, density;

        if (altitude <= 11000) {  // Troposphere
            temperature = 288.15 - 0.00649 * altitude;
            pressure = 101325 * Math.pow((1 - 0.00649 * altitude / 288.15), 5.2561);
        } else if (altitude <= 25000) {  // Stratosphere
            temperature = 216.65;
            pressure = 22632.06 * Math.exp(-0.000157 * (altitude - 11000));
        } else {  // Mesosphere
            temperature = 273.15 - 0.0028 * altitude;
            pressure = 5474.89 * Math.pow((1 - 0.0028 * altitude / 273.15), 5.2561);
        }

        density = pressure / (287.05 * temperature);

        return density;
    }

    /**
     * Does not take into account fins
     * @param extension extension of the airbrakes (0-1)
     * @return rocket's cross-sectional area from airbrake extension
     */
    private static double rocket_area(double extension) {
        return (AIRBRAKES_MAX_AREA * extension) + ROCKET_BASE_AREA;
    }

    /**
     * @param velocity is used to lookup drag value
     * @param fixed_extension MUST BE EITHER 0, 0.5, or 1. Used to determine which function to use and to adjust for rocket area
     * @return drag force value based on curves produced by Ansys at fixed extensions
     */
    private static double lookup_drag(double fixed_extension, double velocity) {
        double drag;

        if (Math.abs(fixed_extension) < TOL) { //0%
            drag = (0.0035 * velocity * velocity) + (0.1317 * velocity) - 5.0119;
        } 
        else if (Math.abs(fixed_extension - 0.5) < TOL) { //50%
            drag = (0.0045 * velocity * velocity) + (0.1031 * velocity) - 3.8231;
        } 
        else { // 100%
            drag = (0.006 * velocity * velocity) + (0.1038 * velocity) - 4.2522;
        }

        return drag / rocket_area(fixed_extension) / air_density(SIM_ALTITUDE); //adjust simulation drag for altitude and extension amount
    }

    /**@return drag force acting on rocket
     * @param extension of air brakes, used for adjusting rocket area and iterpolating Ansys sims (0-1)
     * @param velocity used to lookup drag force from Ansys sims
     * @param altitude used to adjust fluid density since Ansys sims were calculated at 1000m
     * */
    private static double interpolate_drag(double extension, double velocity, double altitude) {
        double drag;
        double dx = 0.5;
        double y_1;
        double y_2;
        double x_1;

        if(extension < 0 || extension > 1 + TOL){
            throw new IndexOutOfBoundsException("airbrakes extension amount was not from 0 to 1");
        }

        if (extension > 0.5) {
            x_1 = 0.5;
            y_1 = lookup_drag(0.5, velocity);
            y_2 = lookup_drag(1, velocity);
        }
        else { // extension < 0.5
            x_1 = 0;
            y_1 = lookup_drag(0, velocity);
            y_2 = lookup_drag(0.5, velocity);
        }

        drag = y_1 + (y_2-y_1) / dx * (extension - x_1);
        drag = air_density(altitude) * rocket_area(extension) * drag; //correct the drag using the actual airbrake extension area and air density

        //System.out.println("Fdrag: " + drag + "N");
        return drag;
    }

    /** TODO: rather hack way of overriding for OR to do its step. This should be replaced ideally by overriding OR's drag calculation, rather than just the CD parameter
     * @return Cd value corresponding to interpolated drag force
     */
    public static double interpolate_cd(double extension, double velocity, double altitude){
        double drag_force = interpolate_drag(extension, velocity, altitude);
        return 2 * drag_force / (air_density(altitude) * rocket_area(extension) * (velocity * velocity)); //Cd
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
            double Fd = -interpolate_drag(airbrake_ext, states.vel, states.alt); // force of drag (N)

            // to check if altitude is decreasing to exit the loop
            prevAlt = states.alt;

            // update velocity and altitude
            states = rk4(h, Fg+Fd, mass, states);

            // System.out.println("pred alt: " + states.alt + "m");
        }

        return states.alt;
    }
}


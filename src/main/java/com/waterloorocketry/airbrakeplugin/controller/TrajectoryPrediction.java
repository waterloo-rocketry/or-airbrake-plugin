package com.waterloorocketry.airbrakeplugin.controller;

public class TrajectoryPrediction {
    /**
     *  @return derivative of velocity: acceleration
     */
    private static double velocity_derivative(double force, double mass) {
        return force/mass;
    }

    /**
     * rk4 method to integrate altitude from velocity, and integrate velocity from acceleration (force/mass)
     * @param h time step to integrate over
     * @param force sum of forces acting on rocket at given time (N)
     * @param mass of rocket (kg)
     * @param integrals, altitude (m) integrals[0] and velocity (m/s) integrals[1]
     */
    private static void rk4(double h, double force, double mass, double[] integrals) {
        double ka1 = h * integrals[1];
        double kv1 = h * velocity_derivative(force, mass);

        double ka2 = h * (integrals[1] + h*ka1/2);
        double kv2 = h * velocity_derivative(force + h*kv1/2, mass);

        double ka3 = h * (integrals[1] + h*ka2/2);
        double kv3 = h * velocity_derivative(force + h*kv2/2, mass);

        double ka4 = h * (integrals[1] + h*ka3);
        double kv4 = h * velocity_derivative(force + h*kv3, mass);

        integrals[0] = (integrals[0] + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        integrals[1] = (integrals[1] + (kv1 + 2*kv2 + 2*kv3 + kv4)/6);
    }

    /** @return acceleration due to gravity */
    private static double gravitational_acceleration(double altitude) {
        return 9.80665 * Math.pow(6371009 / ( 6371009 + altitude), 2);
    }

    /** @return air density from altitude */
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

    /** @return  force of drag from drag coefficient, altitude, and velocity */
    private static double drag_force(double drag_coef, double altitude, double velocity) {
        double ref_area = 0.01824; // reference area (m^2)
        double fluid_dens = air_density(altitude); // air density (kg/m^3)

        // Fd = 0.5 * Cd * A * p * v^2
        return -0.5 * drag_coef * ref_area * fluid_dens * velocity * velocity;
    }

    /** @return max apogee from velocity, altitude, and drag coefficient */
    public static double get_max_altitude(double velocity, double altitude, double drag_coef, double mass) {

        double h = 0.01; // interval of change for rk4
        double prevAlt = 0.0; // variable to store previous altitude
        double[] alt_vel = {altitude, velocity}; // array to store altitude and velocity

        while (alt_vel[0] >= prevAlt) {
            // update forces of drag and gravity from new altitude
            double Fg = -gravitational_acceleration(alt_vel[0]) * mass; // force of gravity (N)
            double Fd = drag_force(drag_coef, alt_vel[0], alt_vel[1]); // force of drag (N)

            // to check if altitude is decreasing to exit the loop
            prevAlt = alt_vel[0];

            // update velocity and altitude
            rk4(h, Fg+Fd, mass, alt_vel);
        }

        return alt_vel[0];
    }
}

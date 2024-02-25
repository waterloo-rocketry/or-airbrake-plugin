package com.waterloorocketry.airbrakeplugin.controller;

public class TrajectoryPrediction {
    private double velocity;
    private double altitude;
    private double drag_coef;

    private double mass;

    TrajectoryPrediction(double vel, double alt, double cd, double m) {
        velocity = vel;
        altitude = alt;
        drag_coef = cd;
        mass = m;
    }

    /** @return derivative of velocity: acceleration */
    private double velocity_derivative(double force) {
        return force/mass;
    }
    /** rk4 method to integrate altitude from velocity, and integrate velocity from acceleration (force/mass)
    requires time step h */
    private void rk4(double h, double force) {
        double ka1 = h * velocity;
        double kv1 = h * velocity_derivative(force);

        double ka2 = h * (velocity + h*ka1/2);
        double kv2 = h * velocity_derivative(force + h*kv1/2);

        double ka3 = h * (velocity + h*ka2/2);
        double kv3 = h * velocity_derivative(force + h*kv2/2);

        double ka4 = h * (velocity + h*ka3);
        double kv4 = h * velocity_derivative(force + h*kv3);

        altitude = (altitude + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        velocity = (velocity + (kv1 + 2*kv2 + 2*kv3 + kv4)/6);
    }

    /** calculate acceleration due to gravity from altitude */
    private double gravitational_acceleration() {
        return 9.80665 * Math.pow(6371009 / ( 6371009 + altitude), 2);
    }

    /** Calculate air density from altitude */
    private double air_density() {
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

    /** Calculate force of drag from drag coefficent and altitude */
    private double drag_force() {
        double ref_area = 0.01824; // reference area (m^2)
        double fluid_dens = air_density(); // air density (kg/m^3)

        // Fd = 0.5 * Cd * A * p * v^2
        return -0.5 * drag_coef * ref_area * fluid_dens * velocity * velocity;
    }

    /** calculates max apogee from velocity, altitude, and drag coefficient */
    public double get_max_altitude() {

        double h = 0.01; // interval of change for rk4

        double prevAlt = 0.0; // variable to store previous altitude

        while (altitude >= prevAlt) {
            // update forces of drag and gravity from new altitude
            double Fg = -gravitational_acceleration() * mass; // force of gravity (N)
            double Fd = drag_force(); // force of drag (N)

            // to check if altitude is decreasing to exit the loop
            prevAlt = altitude;

            // update velocity and altitude
            rk4(h, Fg+Fd);
        }

        return altitude;
    }

    public void updateInstance(double vel, double alt, double cd, double m) {
        velocity = vel;
        altitude = alt;
        drag_coef = cd;
        mass = m;
    }
}

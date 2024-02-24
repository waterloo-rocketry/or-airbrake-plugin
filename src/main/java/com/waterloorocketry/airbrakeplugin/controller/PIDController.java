package com.waterloorocketry.airbrakeplugin.controller;

class TrajectoryPrediction {
    private static double velocity;
    private static double altitude;
    private static double drag_coef;

    TrajectoryPrediction(double vel, double alt, double cd) {
        velocity = vel;
        altitude = alt;
        drag_coef = cd;
    }

    // Returns derivative of altitude (displacement): velocity
    // This function is redundant, but it makes rk4 more intuitive
    private static double altitude_derivative(double v) {
        return v;
    }

    // Returns derivative of velocity: acceleration
    private static double velocity_derivative(double force, double mass) {
        return force/mass;
    }

    // rk4 method to integrate altitude (y) from velocity (v) and integrate velocity (v) from acceleration (force/mass)
    // requires time step h
    private void rk4(double h, double force, double mass) {
        double ka1 = h * altitude_derivative(velocity);
        double kv1 = h * velocity_derivative(force, mass);

        double ka2 = h * altitude_derivative(velocity + h*ka1/2);
        double kv2 = h * velocity_derivative(force + h*kv1/2, mass);

        double ka3 = h * altitude_derivative(velocity + h*ka2/2);
        double kv3 = h * velocity_derivative(force + h*kv2/2, mass);

        double ka4 = h * altitude_derivative(velocity + h*ka3);
        double kv4 = h * velocity_derivative(force + h*kv3, mass);

        altitude = (altitude + (ka1 + 2*ka2 + 2*ka3 + ka4)/6);
        velocity = (velocity + (kv1 + 2*kv2 + 2*kv3 + kv4)/6);
    }

    // calculate acceleration due to gravity from altitude
    private double gravitational_acceleration() {
        return 9.80665 * Math.pow(6371009 / ( 6371009 + altitude), 2);
    }

    // Calculate air density from altitude
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

    // Calculate force of drag from drag coefficent and altitude
    private double drag_force() {
        double ref_area = 0.01824; // reference area (m^2)
        double fluid_dens = air_density(); // air density (kg/m^3)

        // Fd = 0.5 * Cd * A * p * v^2
        return -0.5 * drag_coef * ref_area * fluid_dens * velocity * velocity;
    }

    // calculates max apogee from velocity, altitude, and drag coefficient
    public double get_max_altitude() {

        double h = 0.01; // interval of change for rk4

        double mass = 39.564; // mass of rocket after burnout (kg)

        double Fg = -gravitational_acceleration() * mass; // force of gravity (N)
        double Fd = drag_force(); // force of drag (N)

        double prevAlt = 0.0; // variable to store previous altitude

        while (altitude >= prevAlt) {
            // to check if altitude is decreasing to exit the loop
            prevAlt = altitude;

            // update velocity and altitude
            rk4(h, Fg+Fd, mass);

            // update forces of drag and gravity from new altitude
            Fd = drag_force();
            Fg = -gravitational_acceleration()*mass;
        }

        return altitude;
    }
}


public class PIDController implements Controller {
    private static final double Kp = 0.00005;
    private static final double Ti = 2;
    private static final double Td = 0.5;


    private final double targetAltitude;

    public PIDController(double targetAltitude) {
        this.targetAltitude = targetAltitude;
    }

    private static class LastState {
        private final double error;
        private final double time;

        private LastState(double error, double time) {
            this.error = error;
            this.time = time;
        }
    }

    private LastState lastState;
    private double integral = 0.0;

    @Override
    public double calculateTargetExt(double[] flightData, double time) {
        double altitude = new TrajectoryPrediction(flightData[4], flightData[1], 0.5).get_max_altitude();
        double error = targetAltitude - altitude;
        if (lastState != null) {
            integral += (time - lastState.time) * (error + lastState.error) * 0.5;
        }
        lastState = new LastState(altitude, time);

        double derivative = -flightData[4];

        double ans = Kp * (error + integral / Ti + Td * derivative);

        System.out.println("extension " + ans);

        // % airbrake extension [0-1]
        return Math.max(Math.min(ans, 1.0), 0.0);
    }
}

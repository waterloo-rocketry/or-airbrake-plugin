package com.waterloorocketry.airbrakeplugin.controller;

public class TrajectoryPrediction {
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

    /** @return air density (kg/m^3)
     * @param altitude (m)
     * Same method used for PyAnsys simulations, verified to be correct*/
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

    /**@return rocket's cross-sectional area from airbrake extension
     * Does not take into account fins
     * 0% extension: 28.274 (in^2)
     * 100% extension: 36.041 (in^2)
     * */
    private static double rocket_area(double extension) {
        return (0.0774 * extension) + 28.274;
    }

    /**@return drag force value based off Ansys simulations
     * @param velocity is used to lookup drag value
     * @param extension MUST BE EITHER 0, 50, 100. USed to determing which function to use and to adjust for rocket area
     * air_density is adjusted for since all sims were calculated at 1000m
     * */
    private static double lookup_drag(int extension, double velocity) {
        double drag;
        if (extension == 0) {
            drag = (0.0035 * velocity * velocity) + (0.1317 * velocity) - 5.0119;
        } else if (extension == 50) {
            drag = (0.0045 * velocity * velocity) + (0.1031 * velocity) - 3.8231;
        } else { // extension == 100
            drag = (0.006*velocity*velocity) + (0.1038*velocity) - 4.2522;
        }
        return drag / rocket_area(extension) / air_density(1000);
    }

    /**@return drag force acting on rocket
     * @param extension of air brakes, used for adjusting rocket area and iterpolating Ansys sims
     * @param velocity used to lookup drag force from Ansys sims
     * @param altitude used to adjust fluid density since Ansys sims were calculated at 1000m
     * */
    private static double interpolate_drag(double extension, double velocity, double altitude) {
        if (extension == 100 || extension == 50 ||  extension == 0) {
            int ext = (int)extension;
            return air_density(altitude) * rocket_area(ext) * lookup_drag(ext, velocity);
        }
        else if (extension > 50.0) {
            double diff = lookup_drag(100, velocity) - lookup_drag(50, velocity);
            extension = (extension - 50) * 2;
            return air_density(altitude) * rocket_area(extension) * (diff * extension) + lookup_drag(50, velocity);
        }
        else { // extension < 50
            double diff = lookup_drag(50, velocity) - lookup_drag(0, velocity);
            extension *= 2;
            return air_density(altitude) * rocket_area(extension) * (diff * extension) + lookup_drag(0, velocity);
        }
    }

    /** @return max apogee
     * @param velocity vertical velocity (m/s)
     * @param altitude (m)
     * @param airbrake_ext % extension of airbrakes 0-100 NOT 0-1
     * @param mass (kg)*/
    public static double get_max_altitude(double velocity, double altitude, double airbrake_ext, double mass) {

        double h = 0.01; // interval of change for rk4
        double prevAlt = 0.0; // variable to store previous altitude
        double[] alt_vel = {altitude, velocity}; // array to store altitude and velocity

        while (alt_vel[0] >= prevAlt) {
            // update forces of drag and gravity from new altitude
            double Fg = -gravitational_acceleration(alt_vel[0]) * mass; // force of gravity (N)
            double Fd = -interpolate_drag(airbrake_ext, alt_vel[1], alt_vel[0]); // force of drag (N)

            // to check if altitude is decreasing to exit the loop
            prevAlt = alt_vel[0];

            // update velocity and altitude
            rk4(h, Fg+Fd, mass, alt_vel);

            System.out.println(alt_vel[0]);
        }

        return alt_vel[0];
    }
}


package com.waterloorocketry.airbrakeplugin.simulated;

import com.waterloorocketry.airbrakeplugin.util.LazyNavigableMap;

import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Calculator for drag force based on CFD simulated values
 */
public class SimulatedDragForceInterpolator implements Interpolator<SimulatedDragForceInterpolator.Data> {
    /**
     * Data required for the calculation
     */
    public static class Data {
        private final double extension;
        private final double velocity;
        private final double altitude;

        /**
         * Constructs a new `Data` with values
         * @param extension Airbrakes extension value from 0 to 1 inclusive
         * @param velocity Upwards velocity of the rocket in m/s
         * @param altitude Altitude of the rocket in m
         */
        public Data(double extension, double velocity, double altitude) {
            this.extension = extension;
            this.velocity = velocity;
            this.altitude = altitude;
        }
    }

    private final NavigableMap<Double, QuadraticFunction> fs = new TreeMap<>();
    /**
     * The height about sea level that simulations were conducted at, in meters
     */
    private static final double SIM_ALTITUDE = 1000;

    /**
     * Constructs a new `SimulatedDragForceInterpolator` with the simulated values
     */
    public SimulatedDragForceInterpolator() {
        fs.put(0.0, new QuadraticFunction(0.0035, 0.1317, -5.0119));
        fs.put(0.5, new QuadraticFunction(0.0045, 0.1031, -3.8231));
        fs.put(1.0, new QuadraticFunction(0.006, 0.1038, -4.2522));
    }

    @Override
    public double compute(Data data) {
        // The quadratic functions are only evaluated when necessarily, since this map computes values lazily
        NavigableMap<Double, Double> mapExtToInterpolant = new LazyNavigableMap<>(fs, (f) -> f.compute(data.velocity));
        LinearInterpolator interp = new LinearInterpolator(mapExtToInterpolant);
        double dragAtSimAltitude = interp.compute(data.extension);
        return dragAtSimAltitude / AirDensity.getAirDensityAtAltitude(SIM_ALTITUDE)
                * AirDensity.getAirDensityAtAltitude(data.altitude);
    }
}

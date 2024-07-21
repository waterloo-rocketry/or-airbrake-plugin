package com.waterloorocketry.airbrakeplugin.simulated;

import com.waterloorocketry.airbrakeplugin.util.LazyNavigableMap;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
         *
         * @param extension Airbrakes extension value from 0 to 1 inclusive
         * @param velocity  Upwards velocity of the rocket in m/s
         * @param altitude  Altitude of the rocket in m
         */
        public Data(double extension, double velocity, double altitude) {
            this.extension = extension;
            this.velocity = velocity;
            this.altitude = altitude;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "extension=" + extension +
                    ", velocity=" + velocity +
                    ", altitude=" + altitude +
                    '}';
        }
    }

    private final NavigableMap<Double, Cubic2VariableFunction> points = new TreeMap<>();

    /**
     * Constructs a new `SimulatedDragForceInterpolator` with the simulated values
     */
    public SimulatedDragForceInterpolator() {
        // For each extension value, we have a cubic 2-variable polynomial with x as velocity and y as altitude
        // We calculate the drag for velocity and altitude at each extension, then we do linear interpolation
        // to account for the extension value.
        points.put(0.0, new Cubic2VariableFunction(232.2951, 244.7010, -75.1435, 64.3402, -79.5220, 11.7309, -0.8306, -20.4344, 9.7041, -0.6148));
        points.put(0.1, new Cubic2VariableFunction(235.8993, 249.2100, -76.2767, 65.8251, -81.2931, 12.0289, -0.8408, -21.0236, 9.9787, -0.7853));
        points.put(0.2, new Cubic2VariableFunction(245.6886, 260.2967, -80.2111, 69.3746, -85.4361, 12.5705, -0.6199, -22.1676, 10.4297, -0.6666));
        points.put(0.3, new Cubic2VariableFunction(253.9691, 270.2409, -83.5032, 72.3919, -89.3117, 13.3189, -0.7371, -23.2489, 11.0822, -0.7471));
        points.put(0.4, new Cubic2VariableFunction(263.5127, 280.7695, -86.9338, 75.8929, -93.6884, 14.1591, -0.3771, -24.5324, 11.7445, -0.9399));
        points.put(0.5, new Cubic2VariableFunction(272.4592, 290.5670, -90.1040, 78.8810, -97.0343, 14.5126, -0.1988, -25.6374, 12.0348, -0.7327));
        points.put(0.6, new Cubic2VariableFunction(284.8368, 304.7727, -94.4923, 82.4469, -101.4462, 15.1080, -0.6357, -26.5433, 12.4927, -0.8323));
        points.put(0.7, new Cubic2VariableFunction(296.2638, 317.3919, -98.5746, 86.2809, -106.2663, 15.9556, -0.5224, -28.0106, 13.2557, -0.8541));
        points.put(0.8, new Cubic2VariableFunction(303.1856, 325.1022, -100.9674, 88.7552, -109.3743, 16.5627, -0.4253, -28.9892, 13.8034, -0.9447));
        points.put(0.9, new Cubic2VariableFunction(316.4963, 339.6502, -104.5570, 92.4088, -114.1954, 16.9114, -0.5681, -30.4995, 13.9269, -1.2933));
        points.put(1.0, new Cubic2VariableFunction(340.9146, 367.1520, -114.8622, 101.0439, -123.1214, 18.4047, -0.1879, -31.8071, 15.4422, -1.1456));
    }

    @Override
    public double compute(Data data) {
        // The functions are only evaluated when necessarily, since this map computes values lazily
        NavigableMap<Double, Double> perExt = new LazyNavigableMap<>(points, (m) -> {
            double x = (data.velocity - 273.9) / 148.7;
            double y = (data.altitude - 5000) / 3172;
            return m.compute(new Cubic2VariableFunction.Coordinate(x, y));
        });
        return new LinearInterpolator(perExt).compute(data.extension);
    }
}

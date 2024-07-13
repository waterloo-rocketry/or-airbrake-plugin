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
         * @param extension Airbrakes extension value from 0 to 1 inclusive
         * @param velocity Upwards velocity of the rocket in m/s
         * @param altitude Altitude of the rocket in m
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

//    private final NavigableMap<Double, Cubic2VariableFunction> points = new TreeMap<>();
    private final NavigableMap<Double, NavigableMap<Double, NavigableMap<Double, Double>>> points = new TreeMap<>();

    /**
     * Constructs a new `SimulatedDragForceInterpolator` with the simulated values
     */
    public SimulatedDragForceInterpolator() {
        List<String> lines = new ArrayList<>();
        try (FileReader f = new FileReader("./rockets/Final Simulation Result.csv")) {
            try (BufferedReader r = new BufferedReader(f)) {
                while (true) {
                    String l = r.readLine();
                    if (l != null) {
                        lines.add(l);
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read CSV simulation results", e);
        }
        lines = lines.subList(1, lines.size());
        for (String line : lines) {
            String[] values = line.split(",");
            if (values.length != 5) {
                throw new RuntimeException("expected 5 values");
            }
            double ext = Double.parseDouble(values[0]) / 100;
            double alt = Double.parseDouble(values[1]);
            double vel = Double.parseDouble(values[2]);
            double drag = Double.parseDouble(values[3]);
            points.computeIfAbsent(ext, (k) -> new TreeMap<>())
                    .computeIfAbsent(vel, (k) -> new TreeMap<>())
                    .put(alt, drag);
        }
//        points.put(0.0, new Cubic2VariableFunction( 273.9, 148.7, 5000, 3172, 232.2951, 244.7010,  -75.1435, 64.3402, -79.5220, 11.7309, -0.8306, -20.4344, 9.7041, -0.6148));
//        points.put(1.0, new Cubic2VariableFunction(273.9, 148.7, 5000, 3172, 340.9146, 367.1520, -114.8622, 101.0439, -123.1214, 18.4047, -0.1879, -31.8071, 15.4422, -1.1456));
    }

    @Override
    public double compute(Data data) {
        // The functions are only evaluated when necessarily, since this map computes values lazily
//        NavigableMap<Double, Double> perExt = new LazyNavigableMap<>(points, (m) -> {
//            return m.compute(new Cubic2VariableFunction.Coordinate(data.velocity, data.altitude));
//        });
//        double ans = new LinearInterpolator(perExt).compute(data.extension);
//        System.out.println(data);
//        System.out.println("Drag: " + ans);
//        return ans;

        NavigableMap<Double, Double> perExt = new LazyNavigableMap<>(points, (m) -> {
            NavigableMap<Double, Double> perVel = new LazyNavigableMap<>(m,
                    (n) -> new LinearInterpolator(n).compute(data.altitude));
            return new LinearInterpolator(perVel).compute(data.velocity);
        });
        return new LinearInterpolator(perExt).compute(data.extension);
    }
}

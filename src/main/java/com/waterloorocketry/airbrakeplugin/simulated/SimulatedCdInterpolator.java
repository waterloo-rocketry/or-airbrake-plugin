package com.waterloorocketry.airbrakeplugin.simulated;

import com.waterloorocketry.airbrakeplugin.util.LazyNavigableMap;

import java.util.NavigableMap;
import java.util.TreeMap;

public class SimulatedCdInterpolator implements Interpolator<SimulatedCdInterpolator.Data> {
    public static class Data {
        private final double extension;
        private final double velocity;

        public Data(double extension, double velocity) {
            this.extension = extension;
            this.velocity = velocity;
        }
    }

    private final NavigableMap<Double, QuadraticFunction> fs = new TreeMap<>();
    public SimulatedCdInterpolator() {
        fs.put(0.0, new QuadraticFunction(0.0035, 0.1317, -5.0119));
        fs.put(0.5, new QuadraticFunction(0.0045, 0.1031, -3.8231));
        fs.put(1.0, new QuadraticFunction(0.006, 0.1038, -4.2522));
    }

    @Override
    public double compute(Data data) {
        // The quadratic functions are only evaluated when necessarily, since this map computes values lazily
        NavigableMap<Double, Double> mapExtToInterpolant = new LazyNavigableMap<>(fs, (f) -> f.compute(data.velocity));
        LinearInterpolator interp = new LinearInterpolator(mapExtToInterpolant);
        return interp.compute(data.extension);
    }
}

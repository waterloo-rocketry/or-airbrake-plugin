package com.waterloorocketry.airbrakeplugin.simulated;

import java.util.Map;
import java.util.NavigableMap;

public class LinearInterpolator implements Interpolator<Double> {
    private final NavigableMap<Double, Double> data;

    public LinearInterpolator(NavigableMap<Double, Double> data) {
        this.data = data;
    }

    @Override
    public double compute(Double x) {
        if (x.isNaN()) {
            throw new IllegalArgumentException();
        }
        Map.Entry<Double, Double> floor = data.floorEntry(x);
        if (floor == null) {
            throw new IndexOutOfBoundsException();
        }
        Map.Entry<Double, Double> ceil = data.ceilingEntry(x);
        if (ceil == null) {
            throw new IndexOutOfBoundsException();
        }
        double x1 = floor.getKey();
        double y1 = floor.getValue();
        double x2 = ceil.getKey();
        double y2 = ceil.getValue();
        if (x1 == x2) {
            return y1;
        }
        return (x - x1) * (y2 - y1) / (x2 - x1) + y1;
    }
}

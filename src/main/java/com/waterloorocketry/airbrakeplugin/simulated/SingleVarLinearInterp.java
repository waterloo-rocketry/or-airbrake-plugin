package com.waterloorocketry.airbrakeplugin.simulated;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Single-variable linar interpolation calculator
 */
public class SingleVarLinearInterp {
    private final double[] xs;
    private final double[] fs;

    /**
     * Constructs the calculator with given keys and values
     * @param xs Keys
     * @param fs Corresponding values
     */

    public SingleVarLinearInterp(double[] xs, double[] fs) {
        if (xs.length != fs.length) {
            throw new IllegalArgumentException("keys and values must be of the same size");
        }
        if (xs.length < 2) {
            throw new IllegalArgumentException("at least two key/value pairs must be given");
        }
        for (int i = 0; i < xs.length; i++) {
            if (Double.isNaN(xs[i])) {
                throw new IllegalArgumentException("key cannot be nan");
            }
            if (Double.isNaN(fs[i])) {
                throw new IllegalArgumentException("value cannot be nan");
            }
        }
        for (int i = 0; i + 1 < xs.length; i++) {
            if (!(xs[i] < xs[i + 1])) {
                throw new IllegalArgumentException("xs must be strictly increasing");
            }
        }
        this.xs = xs;
        this.fs = fs;
    }

    public double compute(double x) {
        if (Double.isNaN(x)) {
            throw new IllegalArgumentException("x is nan");
        }
        int i = Arrays.binarySearch(xs, x);
        if (i >= 0) {
            return fs[i];
        }
        int insertionPoint = -(i + 1);
        if (insertionPoint == 0 || insertionPoint == xs.length) {
            throw new IndexOutOfBoundsException("out of bounds");
        }
        double run = x - xs[insertionPoint - 1];
        double slope = (fs[insertionPoint] - fs[insertionPoint - 1]) / (xs[insertionPoint] - xs[insertionPoint - 1]);
        return slope * run + fs[insertionPoint - 1];
    }
}

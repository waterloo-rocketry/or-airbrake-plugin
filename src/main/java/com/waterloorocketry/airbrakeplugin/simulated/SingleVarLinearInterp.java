package com.waterloorocketry.airbrakeplugin.simulated;

import java.util.Arrays;

/**
 * Single-variable linear interpolation calculator
 * The function to be interpolated is computed on demand based on a context.
 * @param <C> Context for calculating the function values
 * @param <D> Data that calculates the function values based on a context
 */
public class SingleVarLinearInterp<C, D extends SingleVarLinearInterp.StepData<C>> {
    /**
     * Computes the function value to be interpolated, based on a given context
     * @param <C> Context to compute value with
     */
    public interface StepData<C> {
        /**
         * Computes the value
         * @param context Context
         * @return Function value
         */
        double compute(C context);
    }

    /**
     * A constant data value
     * For context, just pass in null
     */
    public static class ConstantValue implements StepData<Void> {
        private final double v;

        /**
         * @param v The value which is computed every time
         */
        public ConstantValue(double v) {
            this.v = v;
        }

        @Override
        public double compute(Void context) {
            return v;
        }
    }

    private final double[] xs;
    private final D[] fs;

    /**
     * Constructs the calculator with given keys and values
     * @param xs Keys
     * @param fs Corresponding value calculators
     */

    public SingleVarLinearInterp(double[] xs, D[] fs) {
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
            if (fs[i] == null) {
                throw new IllegalArgumentException("value cannot be null");
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

    /**
     * Computes the interpolated value
     * @param context Context for the data calculations
     * @param x Input value to the function
     * @return Interpolated output value to the function
     */
    public double compute(C context, double x) {
        if (Double.isNaN(x)) {
            throw new IllegalArgumentException("x is nan");
        }
        int i = Arrays.binarySearch(xs, x);
        if (i >= 0) {
            return fs[i].compute(context);
        }
        int insertionPoint = -(i + 1);
        if (insertionPoint == 0 || insertionPoint == xs.length) {
            throw new IndexOutOfBoundsException("out of bounds");
        }
        double run = x - xs[insertionPoint - 1];
        double v1 = fs[insertionPoint].compute(context);
        double v0 = fs[insertionPoint - 1].compute(context);
        double slope = (v1 - v0) / (xs[insertionPoint] - xs[insertionPoint - 1]);
        return slope * run + v0;
    }
}

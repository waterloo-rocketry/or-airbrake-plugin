package com.waterloorocketry.airbrakeplugin.simulated;

public class QuadraticFunction implements Interpolator<Double> {
    private final double a;
    private final double b;
    private final double c;

    public QuadraticFunction(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }


    @Override
    public double compute(Double x) {
        return a * x * x + b * x + c;
    }
}

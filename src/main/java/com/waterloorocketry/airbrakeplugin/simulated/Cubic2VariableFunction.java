package com.waterloorocketry.airbrakeplugin.simulated;

public class Cubic2VariableFunction implements Interpolator<Cubic2VariableFunction.Coordinate> {
    private final double p00;
    private final double p10;
    private final double p01;
    private final double p20;
    private final double p11;
    private final double p02;
    private final double p30;
    private final double p21;
    private final double p12;
    private final double p03;

    public static class Coordinate {
        public double x;
        public double y;

        Coordinate(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }

    Cubic2VariableFunction(double p00, double p10, double p01, double p20, double p11, double p02, double p30, double p21, double p12, double p03) {
        this.p00 = p00;
        this.p10 = p10;
        this.p01 = p01;
        this.p20 = p20;
        this.p11 = p11;
        this.p02 = p02;
        this.p30 = p30;
        this.p21 = p21;
        this.p12 = p12;
        this.p03 = p03;
    }

    @Override
    public double compute(Coordinate coordinate) {
        double x = coordinate.x;
        double y = coordinate.y;
        return p00 + p10 * x + p01 * y + p20 * x * x + p11 * x * y + p02 * y * y + p30 * x * x * x + p21 * x * x * y + p12 * x * y * y + p03 * y * y * y;
    }
}

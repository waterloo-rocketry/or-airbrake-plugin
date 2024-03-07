package com.waterloorocketry.airbrakeplugin.controller;

public class AlwaysOpenController implements Controller {
    private final double ext;
    public AlwaysOpenController(double ext) {
        this.ext = ext;
    }
    @Override
    public double calculateTargetExt(double[] flightData, double timestamp) {
        return ext;
    }
}

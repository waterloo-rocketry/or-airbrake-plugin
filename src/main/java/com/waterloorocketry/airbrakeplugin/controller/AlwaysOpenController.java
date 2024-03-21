package com.waterloorocketry.airbrakeplugin.controller;

public class AlwaysOpenController implements Controller {
    private final double ext;
    public AlwaysOpenController(double ext) {
        this.ext = ext;
    }

    @Override
    public double calculateTargetExt(RocketState rocketState, double timestamp, double extension) {
        return ext;
    }
}

package com.waterloorocketry.airbrakeplugin.controller;

public class AlwaysOpenController implements Controller {

    @Override
    public double calculateTargetExt(double[] flightData, double timestamp) {
        return 1;
    }
}
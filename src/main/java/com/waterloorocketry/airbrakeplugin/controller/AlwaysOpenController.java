package com.waterloorocketry.airbrakeplugin.controller;

public class AlwaysOpenController implements Controller {

    @Override
    public double calculateTargetExt(RocketState rocketState, double timestamp, double extension) {
        return 1;
    }
}

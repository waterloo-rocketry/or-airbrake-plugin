package com.waterloorocketry.airbrakeplugin.simulated;

public class Noise {
    private final double stddevPositionZ;
    private final double stddevVelocityX;
    private final double stddevVelocityY;
    private final double stddevVelocityZ;

    public Noise(double stddevPositionZ, double stddevVelocityX, double stddevVelocityY, double stddevVelocityZ) {
        this.stddevPositionZ = stddevPositionZ;
        this.stddevVelocityX = stddevVelocityX;
        this.stddevVelocityY = stddevVelocityY;
        this.stddevVelocityZ = stddevVelocityZ;
    }

    public double getStddevPositionZ() {
        return stddevPositionZ;
    }

    public double getStddevVelocityX() {
        return stddevVelocityX;
    }

    public double getStddevVelocityY() {
        return stddevVelocityY;
    }

    public double getStddevVelocityZ() {
        return stddevVelocityZ;
    }
}

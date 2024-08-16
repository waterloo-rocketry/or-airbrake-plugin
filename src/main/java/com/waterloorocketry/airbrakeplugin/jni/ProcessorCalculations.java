package com.waterloorocketry.airbrakeplugin.jni;

public class ProcessorCalculations {
    static {
        LibraryLoader.load(ProcessorCalculations.class, "processor_calculations");
    }

    public static native float interpolateDrag(float extension, float velocity, float altitude);

    public static native float getMaxAltitude(float extRef, float velY, float velX, float altitude);

    public static class ControllerState {
        public float controllerTermI;
        public float lastError;
        public float lastMs;
        public boolean begun;

        public native void init();

        public native float updateController(float kp, float ki, float kd, float iSatmax, float extRef, float timeMs, float trajectory, float target);
    }
}

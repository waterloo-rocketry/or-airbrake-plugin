package com.waterloorocketry.airbrakeplugin.jni;

public class ProcessorCalculations {
    static {
        LibraryLoader.load(ProcessorCalculations.class, "processor_calculations");
    }

    public static native float interpolateDrag(float extension, float velocity, float altitude);

    public static native float getMaxAltitude(float velY, float velX, float altitude, float airbrakeExt, float mass);

    public static class ControllerState {
        public float controllerTermI;
        public float lastError;
        public float lastMs;
        public boolean begun;

        public native void init();

        public native float updateController(float timeMs, float error);
    }
}

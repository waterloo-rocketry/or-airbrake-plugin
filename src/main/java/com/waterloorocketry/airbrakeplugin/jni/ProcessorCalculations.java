package com.waterloorocketry.airbrakeplugin.jni;

public class ProcessorCalculations {
    static {
        LibraryLoader.load(ProcessorCalculations.class, "processor_calculations");
    }

    public static native float interpolateDrag(float extension, float velocity, float altitude);
}

#include "com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations.h"

#include "./cansw_processor_stm/STM32Cube/Calculations/trajectory_lib.h"

JNIEXPORT jfloat JNICALL Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_interpolateDrag(
    JNIEnv *env, jclass cls, jfloat extension, jfloat velocity, jfloat altitude) {
    return interpolate_drag(extension, velocity, altitude);
}

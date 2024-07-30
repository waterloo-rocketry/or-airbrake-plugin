#include "./cansw_processor_stm/STM32Cube/Calculations/controller_lib.h"
#include "./cansw_processor_stm/STM32Cube/Calculations/trajectory_lib.h"
#include "com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations.h"
#include "com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_ControllerState.h"

JNIEXPORT jfloat JNICALL
Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_interpolateDrag(
    JNIEnv* env, jclass cls, jfloat extension, jfloat velocity,
    jfloat altitude) {
    return interpolate_drag(extension, velocity, altitude);
}

JNIEXPORT jfloat JNICALL
Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_getMaxAltitude(
    JNIEnv*, jclass, jfloat velY, jfloat velX, jfloat altitude, jfloat ext,
    jfloat mass) {
    return get_max_altitude(velY, velX, altitude, ext, mass);
}

void loadControllerState(ControllerState* state, JNIEnv* env, jclass cls,
                         jobject obj) {
    state->controller_term_I =
        env->GetFloatField(obj, env->GetFieldID(cls, "controllerTermI", "F"));
    state->last_error =
        env->GetFloatField(obj, env->GetFieldID(cls, "lastError", "F"));
    state->last_ms =
        env->GetFloatField(obj, env->GetFieldID(cls, "lastMs", "F"));
    state->begun =
        env->GetBooleanField(obj, env->GetFieldID(cls, "begun", "Z"));
}

void saveControllerState(ControllerState* state, JNIEnv* env, jclass cls,
                         jobject obj) {
    env->SetFloatField(obj, env->GetFieldID(cls, "controllerTermI", "F"),
                       state->controller_term_I);
    env->SetFloatField(obj, env->GetFieldID(cls, "lastError", "F"),
                       state->last_error);
    env->SetFloatField(obj, env->GetFieldID(cls, "lastMs", "F"),
                       state->last_ms);
    env->SetBooleanField(obj, env->GetFieldID(cls, "begun", "Z"), state->begun);
}

JNIEXPORT void JNICALL
Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_00024ControllerState_init(
    JNIEnv* env, jobject obj) {
    jclass cls = env->GetObjectClass(obj);
    ControllerState state;
    loadControllerState(&state, env, cls, obj);
    controllerStateInit(&state);
    saveControllerState(&state, env, cls, obj);
}

JNIEXPORT jfloat JNICALL
Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_00024ControllerState_updateController(
    JNIEnv* env, jobject obj, jfloat time_ms, jfloat error) {
    jclass cls = env->GetObjectClass(obj);
    ControllerState state;
    loadControllerState(&state, env, cls, obj);
    float ans = updateController(&state, time_ms, error);
    saveControllerState(&state, env, cls, obj);
    return ans;
}
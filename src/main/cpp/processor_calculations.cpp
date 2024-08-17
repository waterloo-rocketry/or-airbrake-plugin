#include "./cansw_processor_stm/STM32Cube/Calculations/controller_lib.h"
#include "./cansw_processor_stm/STM32Cube/Calculations/trajectory_lib.h"
#include "com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations.h"
#include "com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_ControllerState.h"

JNIEXPORT jfloat JNICALL
Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_interpolateDrag(
    JNIEnv* env, jclass cls, jfloat extension, jfloat velocity,
    jfloat altitude) {
    return dragAccel_m_s2(extension, velocity, altitude);
}

JNIEXPORT jfloat JNICALL
Java_com_waterloorocketry_airbrakeplugin_jni_ProcessorCalculations_getMaxAltitude(
    JNIEnv*, jclass, jfloat extRef, jfloat velY, jfloat velX, jfloat altitude) {
    return getMaxAltitude_m(extRef, velY, velX, altitude);
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
    JNIEnv* env, jobject obj, jfloat kp, jfloat ki, jfloat kd, jfloat i_satmax,
    jfloat ext_ref, jfloat time_ms, jfloat trajectory, jfloat target) {
    jclass cls = env->GetObjectClass(obj);
    ControllerParams params = {
        .kp = kp, .ki = ki, .kd = kd, .i_satmax = i_satmax, .ext_ref = ext_ref};
    ControllerState state;
    loadControllerState(&state, env, cls, obj);
    float ans = updateController(&params, &state, time_ms, trajectory, target);
    saveControllerState(&state, env, cls, obj);
    return ans;
}
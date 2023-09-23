#include <jni.h>

JNIEXPORT jint JNICALL Java_com_raffa064_engine_core_SquareLib_soma(JNIEnv *env, jobject obj, jint a, jint b) {
    return a + b;
}

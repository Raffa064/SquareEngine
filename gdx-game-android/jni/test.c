#include <jni.h>

JNIEXPORT jint JNICALL Java_com_raffa064_engine_JNI_soma(JNIEnv *env, jobject obj, jint a, jint b) {
    return a + b;
}

JNIEXPORT jint JNICALL Java_com_raffa064_engine_JNI_pow(JNIEnv *env, jobject obj, jint a, jint b) {
    jint v = a;
	
	for (int i = 0; i < b-1; i++) {
		v *= a;
	}
	
	return v;
}


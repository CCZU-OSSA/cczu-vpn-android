//
// Created by moli on 2024/11/27.
//

#include <jni.h>
#include <string>
#include "utils.h"


char *byteArray2Char(JNIEnv *env, jbyteArray array) {
    jboolean copy = true;
    int arrayLength = env->GetArrayLength(array);
    jbyte *arrayData = env->GetByteArrayElements(array, &copy);
    char *data = new char[arrayLength];
    memcpy(data, arrayData, arrayLength);
    env->ReleaseByteArrayElements(array, arrayData, 0);
    return data;
}

jbyteArray char2ByteArray(JNIEnv *env, char *data, int size) {
    jbyteArray array = env->NewByteArray(size);
    env->SetByteArrayRegion(array, 0, size, reinterpret_cast<const jbyte *>(data));
    return array;
}
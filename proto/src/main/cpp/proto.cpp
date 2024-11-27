#include <jni.h>
#include <string>
#include "log.h"
#include "enlink.h"


jstring enlinkVersion(JNIEnv *env, jclass ) {
    const char *versionChar = version();
    jstring versionStr = env->NewStringUTF(versionChar);
    return versionStr;
}


static JNINativeMethod gMethods[] = {
        {
                "version",
                "()Ljava/lang/String;",
                (void *) enlinkVersion
        }
};


extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    jclass enlinkProtocol = env->FindClass("io/github/cczuossa/vpn/proto/EnlinkProtocol");
    if (enlinkProtocol == nullptr) {
        return -1;
    }
    if (env->RegisterNatives(enlinkProtocol, gMethods, 1) < 0) {
        return -1;
    }

    return JNI_VERSION_1_6;
}
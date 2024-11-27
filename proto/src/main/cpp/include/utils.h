//
// Created by moli on 2024/11/27.
//

#ifndef CCZU_VPN_ANDROID_UTILS_H
#define CCZU_VPN_ANDROID_UTILS_H

#endif //CCZU_VPN_ANDROID_UTILS_H

#include <jni.h>

char *byteArray2Char(JNIEnv *, jbyteArray);

jbyteArray char2ByteArray(JNIEnv *, char *, int);
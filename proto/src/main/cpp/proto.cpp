#include <jni.h>
#include <string>
#include "log.h"
#include "utils.h"
#include "enlink.h"


extern "C" JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }

    return JNI_VERSION_1_6;
}


extern "C"
JNIEXPORT jstring JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_version(JNIEnv *env, jclass clazz) {
    return env->NewStringUTF(version());
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_startService(JNIEnv *env, jclass clazz,
                                                              jstring user, jstring pass) {
    jboolean copy = true;
    const char *userChar = env->GetStringUTFChars(user, &copy);
    const char *userPass = env->GetStringUTFChars(pass, &copy);
    return start_service(userChar, userPass);
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_proxyService(JNIEnv *env, jclass clazz) {
    char *proxyServer = proxy_server();
    int size = strlen(proxyServer);
    return char2ByteArray(env, proxyServer, size);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_sendPacket(JNIEnv *env, jclass clazz,
                                                            jbyteArray content, jint size) {
    return send_packet(byteArray2Char(env, content), size);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_sendTCPPacket(JNIEnv *env, jclass clazz,
                                                               jbyteArray content, jint size) {

    return send_tcp_packet(byteArray2Char(env, content), size);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_sendHeartbeat(JNIEnv *env, jclass clazz) {
    return send_heartbeat();
}
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_receivePacket(JNIEnv *env, jclass clazz,
                                                               jint size) {
    return char2ByteArray(env, receive_packet(size), size);
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_serviceAvailable(JNIEnv *env, jclass clazz) {
    return service_available();
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_stopService(JNIEnv *env, jclass clazz) {
    return stop_service();
}
extern "C"
JNIEXPORT jboolean JNICALL
Java_io_github_cczuossa_vpn_proto_EnlinkProtocol_webvpnAvailable(JNIEnv *env, jclass clazz) {
    return webvpn_available();
}
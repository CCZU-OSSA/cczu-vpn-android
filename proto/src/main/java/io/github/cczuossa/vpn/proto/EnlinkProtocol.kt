package io.github.cczuossa.vpn.proto

import androidx.annotation.Keep

class EnlinkProtocol {

    /**
     * A native method that is implemented by the 'proto' native library,
     * which is packaged with this application.
     */


    companion object {
        // Used to load the 'proto' library on application startup.
        init {
            System.loadLibrary("cczuvpnproto")
            System.loadLibrary("native")
        }

        // cczuvpnproto版本
        @JvmStatic
        external fun version(): String

        // 启动服务
        @JvmStatic
        external fun startService(user: String, pass: String): Boolean

        // 获取代理信息
        @JvmStatic
        external fun proxyService(): ByteArray

        // 发送数据包
        @JvmStatic
        external fun sendPacket(content: ByteArray, size: Int): Boolean

        // 发送TCP数据包
        @JvmStatic
        external fun sendTCPPacket(content: ByteArray, size: Int): Boolean

        // 发送心跳
        @JvmStatic
        external fun sendHeartbeat(): Boolean

        // 收取数据包
        @JvmStatic
        external fun receivePacket(size: Int): ByteArray

        // 获取服务存活状态
        @JvmStatic
        external fun serviceAvailable(): Boolean

        // 停止服务
        @JvmStatic
        external fun stopService(): Boolean

        // 获取webvpn存活状态
        @JvmStatic
        external fun webvpnAvailable(): Boolean
    }
}
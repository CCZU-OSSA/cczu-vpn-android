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

        @JvmStatic
        external fun version(): String

        @JvmStatic
        external fun startService(user: String, pass: String): Boolean

        @JvmStatic
        external fun proxyService(): ByteArray

        @JvmStatic
        external fun sendPacket(content: ByteArray, size: Int): Boolean

        @JvmStatic
        external fun sendTCPPacket(content: ByteArray, size: Int): Boolean

        @JvmStatic
        external fun sendHeartbeat(): Boolean

        @JvmStatic
        external fun receivePacket(size: Int): ByteArray

        @JvmStatic
        external fun serviceAvailable(): Boolean

        @JvmStatic
        external fun stopService(): Boolean

        @JvmStatic
        external fun webvpnAvailable(): Boolean
    }
}
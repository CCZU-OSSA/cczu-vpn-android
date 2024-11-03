package io.github.cczuossa.vpn.protocol


import android.util.Log
import io.github.cczuossa.vpn.data.EnlinkTunData
import io.github.cczuossa.vpn.protocol.trust.EasyTrustManager
import io.github.cczuossa.vpn.utils.log
import java.net.InetSocketAddress
import javax.net.ssl.SSLContext


object EnlinkVPN {


    val port = 443// 固定端口
    val host = "zmvpn.cczu.edu.cn"// 固定主机
    val whitelist = hashSetOf<String>(// 白名单
        "202.195.100.36"
    )// 白名单ip
    val sslFactory = SSLContext.getInstance("TLS").apply {
        init(null, arrayOf(EasyTrustManager), null)
    }
    var socket = sslFactory.socketFactory.createSocket()// 连接对象


    private var validator: () -> EnlinkTunData =
        { EnlinkTunData("", -1) }// 验证器


    /**
     * 连接但不进行验证
     */
    fun connect() {
        runCatching {
            if (socket.isConnected) socket.close()
            socket = sslFactory.socketFactory.createSocket()
            socket.connect(InetSocketAddress(host, port))
            // 验证并重启服务
            auth()
        }.onFailure {
            //it.printStackTrace()
        }
    }


    fun init(
        user: String,
        token: String,
        callback: (status: Boolean, data: EnlinkTunData, vpn: EnlinkVPN) -> Unit
    ) {
        "try auth vpn: $user, $token".log()
        validator = {
            val output = outputStream()
            val input = inputStream()
            // 写出认证
            output.writeAuth(user, token)
            val data = if (input.authStatus()) {
                val address = input.virtualAddress()
                val mask = input.virtualMask()
                input.others()
                input.drop()
                whitelist.add("0.0.0.0")
                EnlinkTunData(address, mask)
            } else EnlinkTunData("", -1)

            callback.invoke(data.mask > 0, data, this)
            data
        }
        connect()

    }

    fun auth(): EnlinkTunData {
        return validator.invoke()
    }

    fun outputStream(): EnlinkDataOutputStream {
        return EnlinkDataOutputStream(socket.getOutputStream())
    }

    fun inputStream(): EnlinkDataInputStream {
        return EnlinkDataInputStream(socket.getInputStream())
    }

}

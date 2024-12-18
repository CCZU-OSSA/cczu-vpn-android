package io.github.cczuossa.vpn.android.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import androidx.core.content.ContextCompat
import io.github.cczuossa.vpn.android.MainActivity
import io.github.cczuossa.vpn.android.app.readString
import io.github.cczuossa.vpn.android.app.readStringList
import io.github.cczuossa.vpn.android.data.ProxyData
import io.github.cczuossa.vpn.android.data.Status
import io.github.cczuossa.vpn.android.data.SubStatus
import io.github.cczuossa.vpn.android.proxy.ProxyForwarder
import io.github.cczuossa.vpn.android.utils.debug
import io.github.cczuossa.vpn.proto.EnlinkProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

class AppVpnService : VpnService() {
    companion object {
        val receiverFilter = "io.github.cczuossa.vpn.service"
    }

    val user by lazy { readString("user") }
    val pass by lazy { readString("pass") }
    val apps by lazy { readStringList("apps") }

    val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                stopSelf()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //TODO: 发送通知，启动vpn，发送广播
        GlobalScope.launch(Dispatchers.IO) {
            auth()
        }
        return START_STICKY
    }

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        registerReceiver(receiver, IntentFilter().apply {
            addAction(receiverFilter)
        }, ContextCompat.RECEIVER_EXPORTED)
    }

    fun auth() {
        sendStatus(0, 0, Status.CONNECTING, SubStatus.AUTH)
        if (!EnlinkProtocol.serviceAvailable()) {
            // 获取账号密码
            "try auth:$user, pass:$pass".debug()
            if (!EnlinkProtocol.startService(user, pass)) {
                "failed to auth.".debug()
                sendStatus(0, 0, Status.ERROR, SubStatus.AUTH_ERROR)
            } else {
                "try to connecting.".debug()
                sendStatus(0, 0, Status.CONNECTING, SubStatus.CONNECTING)
                val proxyData = EnlinkProtocol.proxyService().toString(StandardCharsets.UTF_8)
                "webvpn: ${EnlinkProtocol.webvpnAvailable()}".debug()
                "service: ${EnlinkProtocol.serviceAvailable()}".debug()
                "proxy: $proxyData".debug()
                if (proxyData.isNotBlank()) {
                    val data = Json.decodeFromString<ProxyData>(proxyData)
                    setup(data)
                }
            }
        }
    }

    fun setup(data: ProxyData) {
        "try init net(32): $data".debug()
        val net = Builder()
            .addRoute("0.0.0.0", 32)
            .addAddress(data.address, 32)
            .addDnsServer(data.dns)
            .setSession("EnlinkVPN")
            .apply {
                if (apps.isNotEmpty()) {
                    apps.forEach { addAllowedApplication(it) }
                }
            }
            .establish()
        if (net == null) {
            sendStatus(0, 0, Status.ERROR, SubStatus.NET_ERROR)
        } else {
            ProxyForwarder.setup(net, this)
            sendStatus(0, 0, Status.FINISHING, SubStatus.FINISHED)
        }
    }

    fun sendStatus(upload: Long, download: Long, status: Status, subStatus: SubStatus) {
        sendBroadcast(Intent(MainActivity.receiverFilter).apply {
            putExtra("status", status.name)// 状态
            putExtra("sub_status", subStatus.name)// 子状态
            putExtra("upload", upload)// 上传速度
            putExtra("download", download)// 下载速度
        })
    }

    override fun onDestroy() {
        ProxyForwarder.close()
        unregisterReceiver(receiver)
        sendStatus(0, 0, Status.STOP, SubStatus.STOP)
    }

}
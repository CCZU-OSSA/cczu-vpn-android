package io.github.cczuossa.vpn.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.ParcelFileDescriptor
import io.github.cczuossa.vpn.data.EnlinkTunData
import io.github.cczuossa.vpn.http.WebVpnClient
import io.github.cczuossa.vpn.protocol.EnlinkForwarder
import io.github.cczuossa.vpn.protocol.EnlinkVPN
import io.github.cczuossa.vpn.ui.MainActivity
import io.github.cczuossa.vpn.utils.ConfigUtils
import io.github.cczuossa.vpn.utils.NotifyUtils
import io.github.cczuossa.vpn.utils.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EnlinkVpnService : VpnService() {
    private val webVpnClient by lazy {
        WebVpnClient(ConfigUtils.str("user"), ConfigUtils.str("pass"))
    }
    private val intent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT + PendingIntent.FLAG_MUTABLE
        )
    }
    lateinit var forwarder: EnlinkForwarder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 服务启动时
        NotifyUtils.create {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {// 安卓15及其以上
                startForeground(0x4f, it, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(0x4f, it)
            }
            GlobalScope.launch {
                // TODO: 启动断线重连
                this@EnlinkVpnService.connect()
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun connect() {
        webVpnClient.login()
        if (webVpnClient.userId().isNotBlank()) {
            EnlinkVPN.init(webVpnClient.user, webVpnClient.gatewayRulesData().data.token) { status, data, vpn ->
                protect(vpn.socket)
                data.dns.add("211.65.64.65")
                // TODO: 添加设置的应用
                val tun = setup(data)
                forwarder = EnlinkForwarder(tun!!.fileDescriptor, vpn.inputStream(), vpn.outputStream())
                forwarder.start()
            }
        }
    }

    private fun setup(data: EnlinkTunData): ParcelFileDescriptor? {
        "tun data: $data".log()
        return Builder()
            .addAddress(data.address, data.mask)
            .setConfigureIntent(intent)
            .setSession("EnlinkVPN")
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setMetered(false)
                }
                data.dns.forEach {
                    "setup vpn dns: $it".log()
                    if (it.isNotBlank() && it != "127.0.0.1") addDnsServer(it.trim())
                }
                data.apps.forEach { if (it.isNotBlank()) addAllowedApplication(it.trim()) }
                //addAllowedApplication("com.mmbox.xbrowser")
                //addAllowedApplication("com.tencent.wework")
            }
            .establish()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return EnlinkVpnServiceBinder(this)
    }

    open class EnlinkVpnServiceBinder(
        val service: EnlinkVpnService
    ) : Binder() {
        fun service(): EnlinkVpnService {
            return this.service
        }
    }
}
package io.github.cczuossa.vpn.service

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.ParcelFileDescriptor
import io.github.cczuossa.vpn.IVpnServiceInterface
import io.github.cczuossa.vpn.data.EnlinkTunData
import io.github.cczuossa.vpn.http.WebVpnClient
import io.github.cczuossa.vpn.protocol.EnlinkForwarder
import io.github.cczuossa.vpn.protocol.EnlinkVPN
import io.github.cczuossa.vpn.ui.MainActivity
import io.github.cczuossa.vpn.utils.ConfigUtils
import io.github.cczuossa.vpn.utils.NotifyUtils
import io.github.cczuossa.vpn.utils.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EnlinkVpnService : VpnService() {
    private val webVpnClient by lazy {
        WebVpnClient(ConfigUtils.str("user"), ConfigUtils.str("pass"))
    }
    private val handler = Handler(Looper.getMainLooper())
    private val intent: PendingIntent by lazy {
        PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT + PendingIntent.FLAG_MUTABLE
        )
    }
    var forwarder: EnlinkForwarder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 服务启动时
        NotifyUtils.create {
            it.setContentIntent(this.intent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {// 安卓15及其以上
                startForeground(0x4f, it.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(0x4f, it.build())
            }
            "start foreground service".log()
            //setup(EnlinkTunData("1.1.123.12", 32))
            auth()
            GlobalScope.launch {
                while (true) {
                    if (forwarder != null && forwarder?.desc?.valid() == false) {
                        delay(60000)
                        connect()
                        auth()
                    }
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        forwarder?.stop()
        stopForeground(0x4f)
    }

    fun connect() {
        if (forwarder != null && forwarder?.desc?.valid() == true) {
            handler.post {
                sendBroadcast(Intent().apply {
                    setAction("io.github.cczuossa.vpn.connected")
                })
            }
            return
        }

        GlobalScope.launch {
            runCatching {
                webVpnClient.login()
                if (webVpnClient.userId().isNotBlank()) {
                    EnlinkVPN.init(webVpnClient.user, webVpnClient.gatewayRulesData().data.token) { status, data, vpn ->
                        data.dns.add("211.65.64.65")
                        data.apps.addAll(ConfigUtils.list("apps"))
                        val tun = setup(data)
                        if (tun == null) {
                            handler.post {
                                sendBroadcast(Intent().apply {
                                    setAction("io.github.cczuossa.vpn.disconnected")
                                })
                            }

                            return@init
                        }
                        protect(vpn.socket)
                        "setup success".log()
                        forwarder =
                            forwarder ?: EnlinkForwarder(tun.fileDescriptor, vpn.inputStream(), vpn.outputStream())
                        forwarder?.update(tun.fileDescriptor)
                        forwarder?.start()
                        handler.post {
                            sendBroadcast(Intent().apply {
                                setAction("io.github.cczuossa.vpn.connected")
                            })
                        }
                        "start forwarder".log()
                    }
                    EnlinkVPN.connect()

                }
            }.onFailure {
                it.printStackTrace()
            }

        }
    }

    private fun setup(data: EnlinkTunData): ParcelFileDescriptor? {
        "tun data: $data".log()
        return Builder()
            .addAddress(data.address, data.mask)
            .addRoute("0.0.0.0", 0)
            .setConfigureIntent(intent)
            .setBlocking(false)
            .setSession("吊大VPN")
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setMetered(false)
                }
                data.dns.forEach {
                    "setup vpn dns: $it".log()
                    if (it.isNotBlank() && it != "127.0.0.1") addDnsServer(it.trim())
                }
                data.apps.forEach {
                    "setup vpn app: $it".log()
                    if (it.isNotBlank()) addAllowedApplication(it.trim())
                }
                //addAllowedApplication("com.mmbox.xbrowser")
                //addAllowedApplication("com.tencent.wework")
            }
            .establish()
    }


    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent) ?: EnlinkVpnServiceBinder(this)
    }

    open class EnlinkVpnServiceBinder(
        val service: EnlinkVpnService
    ) : IVpnServiceInterface.Stub() {

        override fun connect() {
            "aidl service connect".log()
            service.connect()
        }
    }

    private fun auth() {
        EnlinkVPN.auth()
    }
}
package io.github.cczuossa.vpn.service

import android.content.Intent
import android.net.VpnService
import android.os.Binder

class EnlinkVpnService : VpnService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 服务启动时
        // TODO: 检查通知权限，发送通知，转为前台服务
        return START_NOT_STICKY
    }


    open class EnlinkVpnServiceBinder(
        val service: EnlinkVpnService
    ) : Binder() {
        fun service(): EnlinkVpnService {
            return this.service
        }
    }
}
package io.github.cczuossa.vpn.service

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Context.BIND_IMPORTANT
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import io.github.cczuossa.vpn.CCZUVpnAndroid
import io.github.cczuossa.vpn.ui.MainActivity
import io.github.cczuossa.vpn.utils.prepare
import io.github.cczuossa.vpn.utils.toastLong

object ServiceStater {

    fun start(activity: MainActivity) {
        // 拉起并绑定服务
        activity.prepare()
    }

    fun prepare(activity: MainActivity, status: Boolean) {
        if (status) {
            // 绑定并启动服务
            val intent = Intent(activity, EnlinkVpnService::class.java)
            activity.startService(intent)

            activity.bindService(
                intent,
                activity.connection,
                BIND_IMPORTANT
            )
        } else {
            // 拒绝了你用什么！！ [○･｀Д´･ ○]
            activity.toastLong("您拒绝了创建VPN，因此无法继续")
        }
    }
}
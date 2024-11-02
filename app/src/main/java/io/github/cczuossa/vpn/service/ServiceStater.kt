package io.github.cczuossa.vpn.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import io.github.cczuossa.vpn.CCZUVpnAndroid
import io.github.cczuossa.vpn.ui.MainActivity
import io.github.cczuossa.vpn.utils.prepare

object ServiceStater {
    private lateinit var connector: (service: EnlinkVpnService) -> Unit
    private val handler = Handler(Looper.getMainLooper())
    private val connection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder is EnlinkVpnService.EnlinkVpnServiceBinder) {
                    // 获取服务
                    handler.post {
                        connector.invoke(binder.service())
                    }
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                // 服务断开时
                CCZUVpnAndroid.APP.bindService(
                    Intent(CCZUVpnAndroid.APP, EnlinkVpnService::class.java),
                    this,
                    Context.BIND_AUTO_CREATE
                )
            }

        }
    }

    fun start(activity: MainActivity) {
        // 拉起并绑定服务
        activity.prepare { status, _ ->
            if (status) {
                connector = { service ->
                    // 启动成功
                    // TODO: 配置服务并启用转发
                }
                // 绑定并启动服务
                activity.bindService(
                    Intent(activity, EnlinkVpnService::class.java),
                    connection,
                    Context.BIND_AUTO_CREATE
                )
            } else {
                // 拒绝了你用什么！！ [○･｀Д´･ ○]
                // TODO: 弹一个弹窗提醒无法继续使用
            }
        }
    }
}
package io.github.cczuossa.vpn.android.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import androidx.core.content.ContextCompat
import io.github.cczuossa.vpn.android.MainActivity
import io.github.cczuossa.vpn.android.data.Status
import io.github.cczuossa.vpn.android.data.SubStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppVpnService : VpnService() {
    companion object {
        val receiverFilter = "io.github.cczuossa.vpn.service"
    }

    val vpn by lazy {
        Builder()
            .addAddress("1.2.3.4", 32)
            .addRoute("0.0.0.0", 0)
            .establish()
    }

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
            // 下面的都由服务发过来的广播变换
            delay(2000)
            sendStatus(0, 0, Status.CONNECTING, SubStatus.AUTH)
            // 设置vpn连接
            vpn?.fileDescriptor
            delay(2000)
            sendStatus(0, 0, Status.CONNECTING, SubStatus.CONNECTING)
            delay(2000)
            sendStatus(0, 0, Status.FINISHING, SubStatus.FINISHED)
        }
        return START_STICKY
    }

    @SuppressLint("WrongConstant")
    override fun onCreate() {
        registerReceiver(receiver, IntentFilter().apply {
            addAction(receiverFilter)
        }, ContextCompat.RECEIVER_EXPORTED)
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
        vpn?.close()
        sendStatus(0, 0, Status.STOP, SubStatus.STOP)
    }

}
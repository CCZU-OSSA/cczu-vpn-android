package io.github.cczuossa.vpn.android.service

import android.content.Intent
import android.net.VpnService
import io.github.cczuossa.vpn.android.MainActivity
import io.github.cczuossa.vpn.android.data.Status
import io.github.cczuossa.vpn.android.data.SubStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AppVpnService : VpnService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //TODO: 发送通知，启动vpn，发送广播
        GlobalScope.launch(Dispatchers.IO) {
            // 下面的都由服务发过来的广播变换
            delay(2000)
            sendStatus(0, 0, Status.CONNECTING, SubStatus.AUTH)
            // 设置vpn连接
            Builder()
                .addAddress("1.2.3.4", 32)
                .addRoute("0.0.0.0", 0)
                .establish()
            delay(2000)
            sendStatus(0, 0, Status.CONNECTING, SubStatus.CONNECTING)
            delay(2000)
            sendStatus(0, 0, Status.FINISHING, SubStatus.FINISHED)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {

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
        sendStatus(0, 0, Status.STOP, SubStatus.STOP)
    }

}
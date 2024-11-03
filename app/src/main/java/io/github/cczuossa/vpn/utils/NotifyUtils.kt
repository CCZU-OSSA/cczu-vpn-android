package io.github.cczuossa.vpn.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import io.github.cczuossa.vpn.CCZUVpnAndroid
import io.github.cczuossa.vpn.R


object NotifyUtils {

    fun create(callback: (notification: Notification) -> Unit) {
        val notificationManager = CCZUVpnAndroid.APP.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(CCZUVpnAndroid.APP, "vpn_service_notify")
        } else {
            Notification.Builder(CCZUVpnAndroid.APP)
        }
            .setWhen(System.currentTimeMillis())
            .setContentTitle("vpn_service_notify")
            .setContentText("vpn_service_notify")
            .setSmallIcon(R.drawable.ic_check)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel("vpn_service_notify", "VPN服务通知", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notification)
        callback.invoke(notification)
    }

}
package io.github.cczuossa.vpn.android.utils

import android.util.Log

class LogUtils {
}

fun String.debug() {
    Log.i("CCZU_ANDROID_VPN", this)
}
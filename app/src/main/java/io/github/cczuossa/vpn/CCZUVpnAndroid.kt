package io.github.cczuossa.vpn

import android.app.Application

class CCZUVpnAndroid : Application() {

    override fun onCreate() {
        APP = this
        super.onCreate()
    }

    companion object {
        lateinit var APP: CCZUVpnAndroid
    }
}
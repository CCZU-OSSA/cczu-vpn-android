package io.github.cczuossa.vpn.android.app

import android.app.Application

class CApp : Application() {

    companion object {
        var APP: CApp? = null
    }

    override fun onCreate() {
        super.onCreate()
        APP = this
    }
}



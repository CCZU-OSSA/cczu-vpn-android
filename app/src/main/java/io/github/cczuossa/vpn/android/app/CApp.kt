package io.github.cczuossa.vpn.android.app

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class CApp : Application() {

    companion object {
        var APP: CApp? = null
    }

    override fun onCreate() {
        super.onCreate()
        APP = this
    }
}



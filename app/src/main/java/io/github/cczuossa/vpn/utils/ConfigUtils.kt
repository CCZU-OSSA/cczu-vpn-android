package io.github.cczuossa.vpn.utils

import android.content.Context
import io.github.cczuossa.vpn.CCZUVpnAndroid

object ConfigUtils {

    val sp by lazy {
        CCZUVpnAndroid.APP.getSharedPreferences("config", Context.MODE_PRIVATE)
    }

    fun hasString(key: String): Boolean {
        return sp.contains(key) && !sp.getString(key, "").isNullOrBlank()
    }

    fun set(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    fun str(key: String): String {
        return sp.getString(key, "")!!
    }

    fun list(key: String): Set<String> {
        return sp.getStringSet(key, hashSetOf<String>())!!
    }

    fun set(key: String, sets: HashSet<String>) {
        sp.edit().putStringSet(key, sets).apply()
    }

}
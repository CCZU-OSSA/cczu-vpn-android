package io.github.cczuossa.vpn.android.app

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class Config {
}

fun Context.sharedPreferences(config: String = "config"): SharedPreferences {
    return getSharedPreferences(config, MODE_PRIVATE)!!
}

fun Context.readString(key: String): String {
    return sharedPreferences().getString(key, "") ?: ""
}

fun Context.readBoolean(key: String): Boolean {
    return sharedPreferences().getBoolean(key, false)
}

fun Context.readStringList(key: String): MutableSet<String> {
    return hashSetOf<String>().apply {
        addAll(sharedPreferences().getStringSet(key, hashSetOf<String>()) ?: hashSetOf())
    }
}


fun Context.setStringList(key: String, value: Set<String>) {
    sharedPreferences().edit().putStringSet(key, value).apply()
}


fun Context.setString(key: String, value: String) {
    sharedPreferences().edit().putString(key, value).apply()
}

fun Context.setBoolean(key: String, value: Boolean) {
    sharedPreferences().edit().putBoolean(key, value).apply()
}

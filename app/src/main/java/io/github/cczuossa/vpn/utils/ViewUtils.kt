package io.github.cczuossa.vpn.utils

import android.app.UiModeManager
import android.content.Context
import android.content.res.Resources
import android.os.Build

object ViewUtils {
    @JvmStatic
    fun getStatusBarHeight(resources: Resources): Int {
        val id = resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (id > 0) resources.getDimensionPixelSize(id) else 0
    }

    @JvmStatic
    fun isDarkMode(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getSystemService(UiModeManager::class.java).nightMode == UiModeManager.MODE_NIGHT_YES
        } else false
    }
}

fun Int.dp2px(resources: Resources): Int {
    return Math.round(resources.displayMetrics.density * this)
}
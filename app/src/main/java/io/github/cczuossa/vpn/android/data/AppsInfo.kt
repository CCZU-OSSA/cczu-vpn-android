package io.github.cczuossa.vpn.android.data

import android.content.pm.PackageInfo

data class AppsInfo(
    val packageInfo: PackageInfo,
    var checked: Boolean
)

package io.github.cczuossa.vpn.data

data class EnlinkTunData(
    val address: String,
    val mask: Int,
    val dns: ArrayList<String> = arrayListOf(),
    val apps: ArrayList<String> = arrayListOf(),
    val routes: ArrayList<EnlinkTunRouteData> = arrayListOf(),
)

package io.github.cczuossa.vpn.android.data

import kotlinx.serialization.Serializable


@Serializable
data class ProxyData(
    val address: String,
    val mask: String,
    val gateway: String,
    val dns: String,
    val wins: String,
)

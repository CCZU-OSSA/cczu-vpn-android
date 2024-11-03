package io.github.cczuossa.vpn.data

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoData(
    val username: String,
    val userId: String,
    val loginKey: String,
    val sid: String,
)

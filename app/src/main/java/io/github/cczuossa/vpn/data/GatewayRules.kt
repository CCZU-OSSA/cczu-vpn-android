package io.github.cczuossa.vpn.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GatewayRules(
    @SerialName("code")
    val code: String,
    @SerialName("messages")
    val messages: String,
    @SerialName("data")
    val data: GatewayRulesData,
)


@Serializable
data class GatewayRulesData(
    @SerialName("token")
    val token: String,
    @SerialName("server")
    val server: String,
    @SerialName("spa_port")
    val spaPort: String,
    @SerialName("admin_port")
    val adminPort: String,
    @SerialName("filter_rules")
    val filterRules: GatewayRulesDataRules,
)

@Serializable
data class GatewayRulesDataRules(
    @SerialName("in_domain")
    val inDomain: MutableList<String>,
    @SerialName("in_ip_list")
    val inIpList: MutableList<String>,
    @SerialName("in_domain_filter")
    val inDomainFilter: MutableList<String>,
    @SerialName("out_ip_list")
    val outIpList: MutableList<String>,
    @SerialName("out_domain_filter")
    val outDomainFilter: MutableList<String>,
    @SerialName("white_list")
    val whiteList: MutableList<String>,
)

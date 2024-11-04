package io.github.cczuossa.vpn.http

import cn.hutool.core.codec.Base64
import io.github.cczuossa.vpn.data.GatewayRules
import io.github.cczuossa.vpn.data.UserInfoData
import io.github.cczuossa.vpn.utils.log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jsoup.Jsoup

class WebVpnClient(val user: String, val password: String) {
    private val ROOT = "https://zmvpn.cczu.edu.cn"
    private val client = HttpClient {
        followRedirects = true
        install(UserAgent) {
            agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0"
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    //message.log()
                }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true

            })
        }


    }

    suspend fun login() = withContext(Dispatchers.IO) {
        // 先取隐藏参数
        val params = hashMapOf<String, String>().apply {
            this["username"] = user
            this["password"] = Base64.encode(password)
        }
        "check hidden values first".log()
        val req = client.get(ROOT)
        val html = Jsoup.parse(req.bodyAsText())
        val fm1 = html.getElementById("fm1") ?: return@withContext
        val url = fm1.attr("action")
        html.getElementsByClass("form-actions")
            .first()!!
            .getElementsByTag("input")
            .toList()
            .filter { it.attr("type") == "hidden" }
            .forEach {
                params[it.attr("name")] = it.attr("value")
            }
        "hidden values: $params".log()
        "submit form second".log()
        val def = client.post {
            val urlStr = "$ROOT${req.request.url.encodedPath}${url.replace("/sso/login", "")}"
            "submit form to:$urlStr".log()
            url(urlStr)
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(Parameters.build {
                params.forEach { (name, value) -> append(name, value) }
            }))
        }.headers["location"] ?: return@withContext
        client.get(def)
        client.cookies(ROOT).forEach {
            "ck: $it".log()
        }
        Base64.decodeStr(client.cookies(ROOT)["clientInfo"]?.value).log()
    }

    suspend fun userId(): String {
        val baseInfo = client.cookies(ROOT)["clientInfo"]?.value
        if (!baseInfo.isNullOrBlank()) {
            val jsonStr = Base64.decodeStr(baseInfo)
            return Json.decodeFromString<UserInfoData>(jsonStr).userId
        }
        return ""
    }

    suspend fun gatewayRulesData(): GatewayRules {
        return client.get("$ROOT/enlink/api/client/user/terminal/rules/${userId()}")
            .body<GatewayRules>()
    }


}
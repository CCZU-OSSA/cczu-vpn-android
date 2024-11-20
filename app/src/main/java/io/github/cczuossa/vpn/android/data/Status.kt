package io.github.cczuossa.vpn.android.data

enum class Status {
    STOP,// 停止
    START,// 启动
    CONNECTING,// 连接中
    ERROR,// 错误
}

enum class SubStatus {
    INIT,// 初始化
    AUTH,// 验证账号密码
    STARTING,// 启动服务中
    CONNECTING,// 连接VPN中
    FINISHED,// 完成
    AUTHERROR,// 账号或密码错误
    SERVICEERROR,// 服务无法启动
    NETERROR,// 网络异常
    ERROR,// 其他异常
}
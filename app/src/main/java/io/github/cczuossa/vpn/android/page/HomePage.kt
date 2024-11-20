package io.github.cczuossa.vpn.android.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.cczuossa.vpn.android.R
import io.github.cczuossa.vpn.android.data.Status
import io.github.cczuossa.vpn.android.data.SubStatus


@Composable
@Preview
fun HomePage() {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // 标题
            HomeTitle()
            // 状态框
            StatusBroad(Status.START, SubStatus.FINISHED)
        }
    }

}

@Composable
fun StatusBroad(status: Status, sub: SubStatus) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 30.dp, vertical = 15.dp)
            .shadow(elevation = 6.dp, shape = RoundedCornerShape(10.dp))
            .background(color = Color(0xffc3cfe2), shape = RoundedCornerShape(10.dp))
    ) {
        // 替换为lottie
        Image(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "status",
            modifier = Modifier.size(26.dp)
                .padding(start = 30.dp)
                .weight(0.2f)
        )
        Column(
            modifier = Modifier.padding(end = 30.dp, start = 15.dp)
                .weight(0.8f)
        ) {
            Text(text = HomePageActions.invokeStatusTitle(status), fontSize = 16.sp)
            Text(
                text = HomePageActions.invokeSubStatusTitle(sub),
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}


@Composable
fun HomeTitle() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        // 图标
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "logo",
            modifier = Modifier
                .size(35.dp)
        )
        // 标题
        Text(
            text = "吊大VPN",
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}


object HomePageActions {

    fun invokeStatusTitle(status: Status): String {
        return when (status) {
            Status.STOP -> "已停止"
            Status.START -> "运行中"
            Status.CONNECTING -> "连接中"
            Status.ERROR -> "出错了"
        }
    }

    fun invokeSubStatusTitle(status: SubStatus): String {
        return when (status) {
            SubStatus.INIT -> "初始化..."
            SubStatus.AUTH -> "正在验证账号密码..."
            SubStatus.STARTING -> "启动服务中..."
            SubStatus.CONNECTING -> "正在连接到VPN..."
            SubStatus.FINISHED -> "连接完毕"
            SubStatus.AUTHERROR -> "账号或密码错误"
            SubStatus.SERVICEERROR -> "无法启动VPN服务"
            SubStatus.NETERROR -> "网络异常"
            SubStatus.ERROR -> "未知的错误"
        }
    }
}
package io.github.cczuossa.vpn.android.page

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextMotion
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import io.github.cczuossa.vpn.android.MainActivity
import io.github.cczuossa.vpn.android.R
import io.github.cczuossa.vpn.android.app.readString
import io.github.cczuossa.vpn.android.data.Status
import io.github.cczuossa.vpn.android.data.SubStatus
import io.github.cczuossa.vpn.android.service.AppVpnService


@Composable
@Preview
fun HomePage(navController: NavController = rememberNavController()) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // 标题
            HomeTitle()
            // 状态框
            StatusBroad(navController)
            // 主菜单
            HomeMenu(Modifier.weight(1f), navController)
        }
    }

}

@Composable
fun HomeMenu(modifier: Modifier, navController: NavController) {
    Column(
        modifier = modifier.fillMaxWidth()
            .padding(top = 40.dp, bottom = 60.dp)

    ) {
        //Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.LightGray))
        HomeMenuItem("账号管理", R.drawable.ic_account) {
            navController.navigate("account")
        }

        HomeMenuItem("代理应用", R.drawable.ic_apps) {
            navController.navigate("apps")
        }

        HomeMenuItem("设置", R.drawable.ic_settings) {
            navController.navigate("settings")
        }

        HomeMenuItem("关于", R.drawable.ic_about) {
            navController.navigate("about")
        }

    }
}

@Composable
fun HomeMenuItem(title: String, @DrawableRes icon: Int, clickable: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable {
                clickable.invoke()
            }
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 45.dp)
        ) {
            Image(
                painter = painterResource(icon),
                contentDescription = "menu icon $title",
                modifier = Modifier.size(26.dp)
            )
            Text(
                text = title, modifier = Modifier.padding(start = 15.dp)
                    .padding(vertical = 13.dp)
            )
        }
    }
}


@Composable
fun StatusBroad(navController: NavController) {
    val ctx = LocalContext.current
    var status by remember { HomePageActions.STATUS }
    var subStatus by remember { HomePageActions.SUB_STATUS }
    val lottie by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.stop2success))
    val progress by animateLottieCompositionAsState(
        composition = lottie,
        isPlaying = true,
        speed = 1.5f * if (status == Status.ERROR || status == Status.STOP) -1f else 1f,
        iterations = if (status == Status.CONNECTING) LottieConstants.IterateForever else 1,
        cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
        restartOnPlay = true,
        clipSpec =
            when (status) {
                Status.STOP, Status.ERROR -> LottieClipSpec.Progress(0f, 0.125f)
                Status.STARTING -> LottieClipSpec.Progress(0f, 0.25f)
                Status.FINISHING, Status.CONNECTING -> LottieClipSpec.Progress(0.25f, 0.75f)
                Status.START -> LottieClipSpec.Progress(0.75f, 1f)
            }
    )

    if (progress >= 0.24f && status == Status.STARTING) {
        HomePageActions.changeStatusTo(Status.CONNECTING)
    }
    if (progress >= 0.74f && status == Status.FINISHING) {
        HomePageActions.changeStatusTo(Status.START)
    }

    LaunchedEffect(true) {
        ctx.bindService(Intent(ctx, AppVpnService::class.java), HomePageActions.connection, 0)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 30.dp)
            .padding(top = 30.dp)
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(10.dp))
            //TODO: 根据状态改变背景颜色
            .background(color = Color(0xffc3cfe2), shape = RoundedCornerShape(10.dp))
            .clickable {
                if (HomePageActions.STATUS.value == Status.STOP || HomePageActions.STATUS.value == Status.ERROR) {
                    // 检查账号先
                    if (ctx.readString("user").isBlank() || ctx.readString("pass").isBlank()) {
                        ctx.toast("清先设置一个账号和密码")
                        navController.navigate("account")
                    } else {
                        HomePageActions.changeStatusTo(Status.STARTING)
                        HomePageActions.SUB_STATUS.value = SubStatus.INIT
                        // 请求权限先
                        MainActivity.permissionLauncher.launch(MainActivity.REQUEST_PERMISSIONS.toTypedArray())
                    }
                } else if (HomePageActions.STATUS.value == Status.START) {
                    HomePageActions.changeStatusTo(Status.STARTING)
                    HomePageActions.SUB_STATUS.value = SubStatus.INIT
                    //停止服务
                    ctx.stopService(Intent(ctx, AppVpnService::class.java))
                }

            }
    ) {

        LottieAnimation(
            composition = lottie,
            progress = {
                progress
            },
            modifier = Modifier.size(26.dp)
                .padding(start = 30.dp)
                .weight(0.2f)
        )
        Column(
            modifier = Modifier.padding(end = 30.dp, start = 15.dp)
                .weight(0.8f)
        ) {
            AnimatedText(HomePageActions.invokeStatusTitle(status), 16.sp)
            AnimatedText(HomePageActions.invokeSubStatusTitle(subStatus), 13.sp, Modifier.padding(top = 5.dp))
        }
    }
}

@Composable
fun AnimatedText(text: String, fontSize: TextUnit, modifier: Modifier = Modifier) {
    AnimatedContent(
        text,
        transitionSpec = {
            (fadeIn() + slideInHorizontally() togetherWith fadeOut() + slideOutHorizontally()).using(
                SizeTransform(clip = false)
            )
        }
    ) {
        Text(
            text = it,
            fontSize = fontSize,
            style = LocalTextStyle.current.copy(textMotion = TextMotion.Animated),
            modifier = modifier
        )
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
            text = stringResource(R.string.app_name),
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}

object HomePageActions {
    @JvmStatic
    var STATUS = mutableStateOf(Status.STOP)

    @JvmStatic
    var SUB_STATUS = mutableStateOf(SubStatus.STOP)

    val connection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                changeStatusTo(Status.START)
                SUB_STATUS.value = SubStatus.FINISHED
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                changeStatusTo(Status.STOP)
                SUB_STATUS.value = SubStatus.STOP
            }

        }
    }

    fun changeStatusTo(newStatus: Status) {
        STATUS.value = newStatus
    }

    fun invokeStatusTitle(status: Status): String {
        return when (status) {
            Status.STOP -> "未启动"
            Status.START -> "运行中"
            Status.CONNECTING -> "连接中"
            Status.ERROR -> "出错了"
            Status.STARTING -> "启动中"
            Status.FINISHING -> "连接中"
        }
    }

    fun invokeSubStatusTitle(status: SubStatus): String {
        return when (status) {
            SubStatus.INIT -> "初始化..."
            SubStatus.AUTH -> "正在验证账号密码..."
            SubStatus.STARTING -> "启动服务中..."
            SubStatus.CONNECTING -> "正在连接到VPN..."
            SubStatus.FINISHED -> "连接成功"
            SubStatus.AUTH_ERROR -> "账号或密码错误"
            SubStatus.SERVICE_ERROR -> "无法启动VPN服务"
            SubStatus.NET_ERROR -> "网络异常"
            SubStatus.ERROR -> "未知的错误"
            SubStatus.STOP -> "请点击此处启动"
        }
    }
}
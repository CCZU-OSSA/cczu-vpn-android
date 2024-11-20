package io.github.cczuossa.vpn.android.page

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.BuildConfig
import io.github.cczuossa.vpn.android.R

@Preview
@Composable
fun AboutPage(navController: NavController = rememberNavController()) {
    BasePage(navController, "关于") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // 大logo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "about logo",
                modifier = Modifier.size(100.dp)
            )
            // 标题
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 30.sp,
                modifier = Modifier.padding(top = 20.dp)
            )
            Row(modifier = Modifier.padding(top = 10.dp)) {
                // 版本
                Text(
                    text = "Ver ${BuildConfig.VERSION_NAME}",
                    fontSize = 15.sp
                )
                Row(
                    modifier = Modifier.padding(start = 10.dp)
                        .clickable {
                            //TODO: 打开浏览器跳转Github
                        }
                ) {
                    // Github 小章鱼
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background),
                        contentDescription = "github",
                        modifier = Modifier.size(20.dp)
                    )
                    // Github 文字
                    Text(
                        text = "Github",
                        fontSize = 15.sp,
                        textDecoration = TextDecoration.Underline,
                        color = Color.Blue
                    )
                }
            }

            // 版权
            Text(
                text = "©2024 常州大学开源软件协会",
                fontSize = 15.sp,

                )
            // More...
        }
    }
}

object AboutPageActions {
}
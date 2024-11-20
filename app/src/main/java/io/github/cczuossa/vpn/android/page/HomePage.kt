package io.github.cczuossa.vpn.android.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.cczuossa.vpn.android.R


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

        }
    }

}


@Composable
fun HomeTitle() {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .padding(0.dp, 20.dp)
    ) {
        // 图标
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "logo",
            modifier = Modifier
                .width(35.dp).height(35.dp)
        )
        // 标题
        Text(
            text = "吊大VPN",
            fontSize = 30.sp,
            modifier = Modifier.padding(start = 20.dp)
        )
    }
}
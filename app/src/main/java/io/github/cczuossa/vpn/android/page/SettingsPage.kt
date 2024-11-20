package io.github.cczuossa.vpn.android.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview
@Composable
fun SettingsPage(navController: NavController = rememberNavController()) {
    BasePage(navController, "设置") {
        Column(modifier = Modifier.fillMaxSize()) {
            Text("不知道要写点啥捏")
        }
    }
}

object SettingsPageActions {
}
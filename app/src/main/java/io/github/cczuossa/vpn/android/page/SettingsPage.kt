package io.github.cczuossa.vpn.android.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.app.readBoolean
import io.github.cczuossa.vpn.android.app.setBoolean

@Preview
@Composable
fun SettingsPage(navController: NavController = rememberNavController()) {
    val ctx = LocalContext.current
    var settingAllowBypass by remember { mutableStateOf(ctx.readBoolean("allow_bypass")) }
    BasePage(navController, "设置") {
        Column(modifier = Modifier.fillMaxSize()) {
            SwitchSetting("允许应用绕过代理", settingAllowBypass) {
                settingAllowBypass = it
                ctx.setBoolean("allow_bypass", it)
            }
        }
    }
}

@Composable
fun SwitchSetting(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .height(50.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            modifier = Modifier.weight(1f)
                .padding(start = 25.dp)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(end = 25.dp)
        )
    }
}

//object SettingsPageActions
package io.github.cczuossa.vpn.android.page

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.R


@Preview
@Composable
fun AccountPage(navController: NavController = rememberNavController()) {
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    BasePage(navController, "账号设置", actions = {
        ActionButton(R.drawable.ic_check) {
            //TODO: 储存 user 和 pass
        }

    }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AccountTextField(user, "用户名") { user = it }
            AccountTextField(pass, "密码") { pass = it }

        }
    }
}

@Composable
fun AccountTextField(content: String = "", title: String, onValueChange: (String) -> Unit = {}) {
    TextField(
        content,
        onValueChange = onValueChange,
        shape = MaterialTheme.shapes.medium,
        singleLine = true,
        placeholder = {
            Text(title)
        },
        modifier = Modifier.fillMaxWidth()
            .padding(top = 20.dp)
            .padding(horizontal = 30.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

//object AccountPageActions
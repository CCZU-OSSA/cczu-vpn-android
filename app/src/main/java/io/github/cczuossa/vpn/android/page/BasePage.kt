package io.github.cczuossa.vpn.android.page

import android.content.Context
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.R


@Preview
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun BasePage(
    navController: NavController = rememberNavController(),
    title: String = "标题",
    titleStyle: @Composable () -> Unit = { Text(text = title) },
    actions: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = titleStyle, navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        content = {
                            Image(
                                painter = painterResource(id = R.drawable.ic_back),
                                "back $title"
                            )
                        }
                    )
                }, actions = {
                    actions.invoke()
                }, expandedHeight = 64.dp
            )
        },

        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.padding(top = 64.dp))
            content.invoke()
        }
    }
}

@Composable
fun ActionButton(@DrawableRes icon: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Image(
            painter = painterResource(id = icon),
            "action button $icon",
            modifier = Modifier
                .padding(4.dp)
                .size(33.dp)
                .padding(end = 5.dp)
        )
    }
}

//object BasePageActions


fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, length).show()
}
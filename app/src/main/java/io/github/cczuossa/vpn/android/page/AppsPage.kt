package io.github.cczuossa.vpn.android.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.R


@Preview
@Composable
fun AppsPage(navController: NavController = rememberNavController()) {
    var searchBarOpen by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var checkedApps by remember { mutableStateOf(hashSetOf<String>()) }
    //TODO: 状态切换动画: 标题与icon渐变消失，搜索框从图标展开
    BasePage(
        navController,
        if (!searchBarOpen) {
            "代理应用"
        } else {
            ""
        },
        actions = {
            if (searchBarOpen) {
                AppSearchBar(searchText) {
                    searchText = it
                }
            }
            // 搜索
            ActionButton(R.drawable.ic_search) {
                //TODO: 展开搜索框
                searchBarOpen = !searchBarOpen
            }

            // 全选
            ActionButton(R.drawable.ic_check_all) {
                //TODO: 全选应用
            }

            // 保存
            ActionButton(R.drawable.ic_check) {
                //TODO: 储存 apps
            }

        })
    {
        // 应用列表
        //val packageInfo = LocalContext.current.packageManager.getApplicationInfo(LocalContext.current.packageName, 0)
        val packageName = "test.app"
        AppsItem(
            painter = painterResource(R.drawable.ic_launcher_background),//packageInfo.loadIcon(LocalContext.current.packageManager).toBitmap(),
            name = "test app",//LocalContext.current.packageManager.getApplicationLabel(packageInfo).toString(),
            //packageInfo.packageName,
            checked = checkedApps.contains(packageName),
        ) {
            checkedApps =
                hashSetOf<String>().apply {
                    if (checkedApps.contains(packageName)) {
                        addAll(checkedApps.filter {
                            it != packageName
                        })
                    } else {
                        addAll(checkedApps)
                        add(packageName)
                    }

                }

        }
    }
}


@Composable
fun AppsItem(
    //bitmap: ImageBitmap,
    painter: Painter,
    name: String,
    checked: Boolean = false,
    onChecked: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 图标
        Image(
            //bitmap = bitmap,
            painter = painter,
            contentDescription = "",
            modifier = Modifier.padding(start = 30.dp)
                .size(35.dp)
        )

        // 标题
        Text(
            name,
            modifier = Modifier.weight(1f)
                .padding(18.dp),
            fontSize = 20.sp,
        )

        // 复选框
        Checkbox(
            checked = checked, onCheckedChange = {
                onChecked.invoke()
            },
            modifier = Modifier.padding(end = 30.dp)
        )
    }
}

@Composable
fun AppSearchBar(searchText: String = "", onValueChanged: (String) -> Unit) {
    // TODO: 自定义高度，图标展开后到最后

    TextField(
        searchText,
        modifier = Modifier.height(40.dp)
            .width(210.dp),
        onValueChange = onValueChanged,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

object AppsPageActions {
}
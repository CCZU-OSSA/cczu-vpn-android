package io.github.cczuossa.vpn.android.page

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.VisualTransformation
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
    BasePage(
        navController,
        title = "代理应用",
        actions = {
            Surface {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxHeight()
                        .width(120.dp)
                ) {
                    AnimatedVisibility(
                        searchBarOpen,
                        enter = expandHorizontally(expandFrom = Alignment.Start),
                        exit = fadeOut(),
                        modifier = Modifier.animateContentSize()


                    ) {
                        AppSearchBar(searchText) {
                            searchText = it
                        }
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.padding(start = 8.dp).fillMaxHeight().width(120.dp)
                ) {
                    // 搜索
                    ActionButton(R.drawable.ic_search) {
                        //TODO: 展开搜索框
                        searchBarOpen = !searchBarOpen
                    }
                }


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
        //TODO: 根据searchText筛选应用
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(searchText: String = "", onValueChanged: (String) -> Unit) {
    // TODO: 自定义高度，图标展开后到最后


    BasicTextField(
        value = searchText,
        modifier = Modifier.height(34.dp)
            .width(120.dp),
        onValueChange = onValueChanged,
        decorationBox = {
            TextFieldDefaults.DecorationBox(
                value = searchText,
                innerTextField = it,
                enabled = true,
                singleLine = true,
                visualTransformation = VisualTransformation.None,
                interactionSource = remember { MutableInteractionSource() },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = MaterialTheme.shapes.medium,
                contentPadding = PaddingValues(top = 2.dp, bottom = 2.dp, start = 16.dp, end = 33.dp)
            )
        }
    )
}

object AppsPageActions {
}
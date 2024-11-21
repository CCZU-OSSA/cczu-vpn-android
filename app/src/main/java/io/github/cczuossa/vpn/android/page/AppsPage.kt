package io.github.cczuossa.vpn.android.page

import android.content.pm.PackageInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCancellationBehavior
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import io.github.cczuossa.vpn.android.MainActivity
import io.github.cczuossa.vpn.android.R
import io.github.cczuossa.vpn.android.app.readStringList
import io.github.cczuossa.vpn.android.app.setStringList
import io.github.cczuossa.vpn.android.data.AppsInfo
import io.github.cczuossa.vpn.android.data.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Preview
@Composable
fun AppsPage(navController: NavController = rememberNavController()) {
    var searchBarOpen by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    var checkAll by remember { mutableStateOf(true) }
    val ctx = LocalContext.current
    var apps = remember { mutableStateListOf<AppsInfo>() }
    var searchText by remember { mutableStateOf("") }
    var checkedApps = hashSetOf<String>()
    val lottie by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.stop2success))
    val progress by animateLottieCompositionAsState(
        composition = lottie,
        isPlaying = loading,
        speed = 1.5f,
        iterations = LottieConstants.IterateForever,
        cancellationBehavior = LottieCancellationBehavior.OnIterationFinish,
        restartOnPlay = true,
        clipSpec = LottieClipSpec.Progress(0.25f, 0.75f)
    )
    LaunchedEffect(true) {
        MainActivity.permissionGetAppLauncher.launch(MainActivity.REQUEST_PERMISSIONS_APP.toTypedArray())
    }
    LaunchedEffect(AppsPageActions.allApps.size, searchText) {
        if (AppsPageActions.allApps.isNotEmpty()) {
            loading = true
            //checkedApps.addAll(ctx.readStringList("apps"))
            apps.clear()
            AppsPageActions.allApps.filter { info ->
                ctx.packageManager.getApplicationLabel(info.applicationInfo!!)
                    .toString()
                    .lowercase()
                    .contains(searchText.lowercase()) || info.packageName
                    .lowercase()
                    .contains(searchText.lowercase())
            }.forEach {
                apps.add(AppsInfo(it, checkedApps.contains(it.packageName)))
            }
            loading = false
        }
    }

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
                        searchBarOpen = !searchBarOpen
                    }
                }


            }


            // 全选
            ActionButton(R.drawable.ic_check_all) {
                for (i in 0 until apps.size) {
                    apps[i] = apps[i].copy(checked = checkAll)
                }
                checkAll = !checkAll
            }

            // 保存
            ActionButton(R.drawable.ic_check) {
                apps.forEach { if (it.checked) checkedApps.add(it.packageInfo.packageName) }
                ctx.setStringList("apps", checkedApps)
                ctx.toast("保存完毕")
            }


        })
    {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (loading) Arrangement.Center else Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            //TODO: 根据searchText筛选应用
            // 应用列表
            //val packageInfo = LocalContext.current.packageManager.getApplicationInfo(LocalContext.current.packageName, 0)
            item(loading) {
                AnimatedVisibility(loading, enter = fadeIn(), exit = fadeOut()) {
                    LottieAnimation(
                        composition = lottie,
                        progress = {
                            progress
                        },
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            for (i in 0 until apps.size) {
                val packageInfo = apps[i]
                item(packageInfo.packageInfo.packageName + ".${packageInfo.checked}") {
                    AppsItem(
                        bitmap = packageInfo.packageInfo.applicationInfo!!.loadIcon(ctx.packageManager).toBitmap()
                            .asImageBitmap(),
                        //painter = painterResource(R.drawable.ic_launcher_background),
                        name = ctx.packageManager.getApplicationLabel(packageInfo.packageInfo.applicationInfo!!)
                            .toString(),
                        checked = packageInfo.checked,
                    ) {
                        apps[i] = apps[i].copy(checked = it)
                    }
                }
            }


        }


    }
}


@Composable
fun AppsItem(
    bitmap: ImageBitmap,
    //painter: Painter,
    name: String,
    checked: Boolean = false,
    onChecked: (checked: Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 图标
        Image(
            bitmap = bitmap,
            //painter = painter,
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
                onChecked.invoke(it)
            },
            modifier = Modifier.padding(end = 30.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSearchBar(searchText: String = "", modifier: Modifier = Modifier, onValueChanged: (String) -> Unit) {
    BasicTextField(
        value = searchText,
        modifier = modifier
            .height(34.dp)
            .width(120.dp)
            .focusable(),
        onValueChange = onValueChanged,
        decorationBox = {
            TextFieldDefaults.DecorationBox(
                value = searchText,
                placeholder = { Text("在此搜索") },
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

    val allApps = arrayListOf<PackageInfo>()
}
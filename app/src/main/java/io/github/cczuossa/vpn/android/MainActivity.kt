package io.github.cczuossa.vpn.android

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.cczuossa.vpn.android.data.Status
import io.github.cczuossa.vpn.android.data.SubStatus
import io.github.cczuossa.vpn.android.page.AboutPage
import io.github.cczuossa.vpn.android.page.AccountPage
import io.github.cczuossa.vpn.android.page.AppsPage
import io.github.cczuossa.vpn.android.page.AppsPageActions
import io.github.cczuossa.vpn.android.page.HomePage
import io.github.cczuossa.vpn.android.page.HomePageActions
import io.github.cczuossa.vpn.android.page.SettingsPage
import io.github.cczuossa.vpn.android.page.toast
import io.github.cczuossa.vpn.android.service.AppVpnService
import io.github.cczuossa.vpn.android.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                //putExtra("upload", upload)// 上传速度
                //putExtra("download", download)// 下载速度
                if (intent?.extras != null) {
                    HomePageActions.changeStatusTo(Status.valueOf(intent.extras?.getString("status")!!))
                    HomePageActions.SUB_STATUS.value = SubStatus.valueOf(intent.extras?.getString("sub_status")!!)
                }
            }
        }
    }

    companion object {
        lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
        lateinit var permissionGetAppLauncher: ActivityResultLauncher<Array<String>>
        lateinit var prepareLauncher: ActivityResultLauncher<Intent>
        val receiverFilter = "io.github.cczuossa.vpn.status"
        val REQUEST_PERMISSIONS = arrayListOf(
            Manifest.permission.INTERNET,// 基础网络权限
            Manifest.permission.ACCESS_NETWORK_STATE,// 基础网络状态检查权限
            Manifest.permission.ACCESS_WIFI_STATE,// 基础wifi状态检查权限

        )
        val REQUEST_PERMISSIONS_APP = arrayListOf<String>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                initService()
            } else {
                toast("您拒绝了创建VPN")
            }
        }
        permissionGetAppLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                AppsPageActions.allApps.clear()
                AppsPageActions.allApps.addAll(
                    this.packageManager.getInstalledPackages(0).filter { it.packageName != this.packageName })

            }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.all {
                    Log.d("123", "${it.key}: ${it.value}")
                    it.value
                }) {
                // 申请VPN服务权限
                VpnService.prepare(this)?.let { intent -> prepareLauncher.launch(intent) } ?: initService()
            } else {
                HomePageActions.SUB_STATUS.value = SubStatus.STOP
                HomePageActions.changeStatusTo(Status.STOP)
                toast("由于您拒绝了所需权限，因此应用无法正常运行")
            }
        }
        resolvePermissions()
        enableEdgeToEdge()
        setContent {
            AppTheme {
                // 导航页面
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home",
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable("home") {
                        HomePage(navController)
                    }
                    composable("account") {
                        AccountPage(navController)
                    }
                    composable("apps") {
                        AppsPage(navController)
                    }
                    composable("settings") {
                        SettingsPage(navController)
                    }
                    composable("about") {
                        AboutPage(navController)
                    }
                }


            }
        }
    }

    @SuppressLint("WrongConstant")
    private fun initService() {
        // 注册广播接收器
        registerReceiver(receiver, IntentFilter().apply {
            addAction(receiverFilter)
        }, ContextCompat.RECEIVER_EXPORTED)
        HomePageActions.SUB_STATUS.value = SubStatus.STARTING
        // 启动服务
        startService(Intent(this@MainActivity, AppVpnService::class.java))
    }

    private fun resolvePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {// 安卓9及其以上
            // 添加前台服务权限
            REQUEST_PERMISSIONS.add(Manifest.permission.FOREGROUND_SERVICE)

        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// 安卓11及其以上
            // 查询全部应用权限
            REQUEST_PERMISSIONS_APP.add(Manifest.permission.QUERY_ALL_PACKAGES)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {// 安卓13及其以上
            // 添加通知权限
            REQUEST_PERMISSIONS.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {// 安卓15及其以上
            // 添加特殊前台服务权限
            REQUEST_PERMISSIONS.add(Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE)
        }
        runCatching {
            if (packageManager.getPermissionInfo("com.android.permission.GET_INSTALLED_APPS", 0) != null) {
                // miui特殊适配
                REQUEST_PERMISSIONS_APP.add("com.android.permission.GET_INSTALLED_APPS")
            }
        }
    }
}

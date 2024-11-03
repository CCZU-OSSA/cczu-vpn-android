package io.github.cczuossa.vpn.ui

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.zackratos.ultimatebarx.ultimatebarx.navigationBar
import com.zackratos.ultimatebarx.ultimatebarx.statusBar
import io.github.cczuossa.vpn.R
import io.github.cczuossa.vpn.databinding.ActivityMainBinding
import io.github.cczuossa.vpn.http.WebVpnClient
import io.github.cczuossa.vpn.service.EnlinkVpnService
import io.github.cczuossa.vpn.service.ServiceStater
import io.github.cczuossa.vpn.utils.ConfigUtils
import io.github.cczuossa.vpn.utils.ViewUtils.getStatusBarHeight
import io.github.cczuossa.vpn.utils.ViewUtils.isDarkMode
import io.github.cczuossa.vpn.utils.jump
import io.github.cczuossa.vpn.utils.log
import io.github.cczuossa.vpn.utils.toastLong
import io.github.cczuossa.vpn.view.MainMenuItem
import io.github.cczuossa.vpn.view.StatusBroad
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private lateinit var _binding: ActivityMainBinding
    lateinit var mActivityResult: ActivityResultLauncher<Intent>
    lateinit var mPermissionResult: ActivityResultLauncher<Array<String>>
    var connecting = false

    val REQUEST_PERMISSIONS = arrayListOf(
        Manifest.permission.INTERNET,// 基础网络权限
        Manifest.permission.ACCESS_NETWORK_STATE,// 基础网络状态检查权限
        Manifest.permission.ACCESS_WIFI_STATE// 基础wifi状态检查权限
    )

    private val connection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder is EnlinkVpnService.EnlinkVpnServiceBinder) {
                    _binding.mainStatusBroad.setState(StatusBroad.State.START)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        // 设置状态栏
        immersionStatusBar()
        // 设置导航栏
        immersionNavigationBar()
        // 初始化view
        initView()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {// 安卓9及其以上
            // 添加前台服务权限
            REQUEST_PERMISSIONS.add(Manifest.permission.FOREGROUND_SERVICE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {// 安卓13及其以上
            // 添加通知权限
            REQUEST_PERMISSIONS.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        mActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                ServiceStater.prepare(this, true)
            }
        }
        mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.forEach {
                if (!it.value) {
                    // 一个都不能拒绝哦 (✺ω✺)
                    "has permission was false".log()
                    toastLong("您必须同意全部权限后应用才可以正常运行")
                    return@registerForActivityResult
                }

            }

            "check account".log()
            if (ConfigUtils.hasString("user") && ConfigUtils.hasString("pass")) {
                GlobalScope.launch {
                    "try login".log()
                    WebVpnClient(ConfigUtils.str("user"), ConfigUtils.str("pass")).apply {
                        login()
                        if (userId().isNotBlank()) {
                            ServiceStater.start(this@MainActivity)
                        }
                    }
                }
            } else {
                toastLong("请先设置账号密码后继续")
                _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
                _binding.mainStatusBroad.setTitle("出现错误")
                _binding.mainStatusBroad.setSubTitle("未设置正确的账号")
                GlobalScope.launch {
                    delay(1000)
                    jump(AccountActivity::class.java)
                }
            }

        }
    }


    private fun initView() {
        // 设置状态面板(默认StatusBroad.State.STOP)
        val statusBroad = _binding.mainStatusBroad
        bindService(Intent(this, EnlinkVpnService::class.java), connection, 0)
        val menuRoot = _binding.mainMenuRoot
        statusBroad.setOnClickListener {
            if (connecting) {
                this.toastLong("请稍等...")
                return@setOnClickListener
            }
            connecting = true
            // 先切换ui状态
            statusBroad.changeStateTo(StatusBroad.State.CONNECTING)
            // 尝试检查权限
            mPermissionResult.launch(REQUEST_PERMISSIONS.toTypedArray())
        }
        menuRoot.addView(MainMenuItem(this).apply {
            setTitle("账号")
            setIcon(resources.getDrawable(R.drawable.ic_user, null))
        })
        menuRoot.addView(MainMenuItem(this).apply {
            setTitle("关于")
            setIcon(resources.getDrawable(R.drawable.ic_about, null))
        })
    }

    private fun immersionNavigationBar() {
        navigationBar {
            fitWindow = false
            transparent()
            light = !isDarkMode(this@MainActivity)
        }
    }

    private fun immersionStatusBar() {
        val statusBarHeight = getStatusBarHeight(this.resources)
        // 添加一个和状态栏等高的占位符
        _binding.mainRoot.addView(
            View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    statusBarHeight
                )
            }, 0
        )

        statusBar {
            fitWindow = false
            transparent()
            light = !isDarkMode(this@MainActivity)
        }
    }


}
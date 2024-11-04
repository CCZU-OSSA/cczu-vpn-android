package io.github.cczuossa.vpn.ui

import android.Manifest
import android.R.attr.action
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.zackratos.ultimatebarx.ultimatebarx.navigationBar
import com.zackratos.ultimatebarx.ultimatebarx.statusBar
import io.github.cczuossa.vpn.IVpnServiceInterface
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
        Manifest.permission.ACCESS_WIFI_STATE,// 基础wifi状态检查权限

    )
    val receiverFilter = "io.github.cczuossa.vpn.connected"
    val receiverFilter1 = "io.github.cczuossa.vpn.disconnected"
    val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                _binding.mainStatusBroad.post {
                    if (intent!!.action == receiverFilter) {
                        connecting = true
                        _binding.mainStatusBroad.changeStateTo(StatusBroad.State.START)
                        _binding.mainStatusBroad.setTitle("连接成功")
                        _binding.mainStatusBroad.setSubTitle("已可以正常访问校园网")
                    } else {
                        connecting = false
                        _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
                        _binding.mainStatusBroad.setTitle("错误")
                        _binding.mainStatusBroad.setSubTitle("VPN服务初始化失败")
                    }
                }
            }
        }
    }

    val connection by lazy {
        object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                "service connected".log()
                IVpnServiceInterface.Stub.asInterface(binder).connect()

                _binding.mainStatusBroad.setSubTitle("尝试连接到vpn...")
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                "service disconnected".log()
                connecting = false
                _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
                _binding.mainStatusBroad.setTitle("错误")
                _binding.mainStatusBroad.setSubTitle("VPN服务断开")
                bindService(Intent(this@MainActivity, EnlinkVpnService::class.java), this, BIND_IMPORTANT)
            }

            override fun onBindingDied(name: ComponentName?) {
                "bind died".log()
                connecting = false
                _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
                _binding.mainStatusBroad.setTitle("错误")
                _binding.mainStatusBroad.setSubTitle("VPN服务断开")
                bindService(Intent(this@MainActivity, EnlinkVpnService::class.java), this, BIND_IMPORTANT)
            }

            override fun onNullBinding(name: ComponentName?) {
                "bind null".log()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {// 安卓11及其以上
            // 查询全部应用权限
            REQUEST_PERMISSIONS.add(Manifest.permission.QUERY_ALL_PACKAGES)
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
                REQUEST_PERMISSIONS.add("com.android.permission.GET_INSTALLED_APPS")
            }
        }
        // 注册广播接收器
        registerReceiver(receiver, IntentFilter().apply {
            addAction(receiverFilter)
            addAction(receiverFilter1)
        }, ContextCompat.RECEIVER_EXPORTED)
        mActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                ServiceStater.prepare(this, true)
            }
        }
        mPermissionResult = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            "permissions: $it".log()
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
                            "try start service".log()
                            _binding.mainStatusBroad.post {
                                _binding.mainStatusBroad.setSubTitle("尝试启动服务...")
                            }
                            ServiceStater.start(this@MainActivity)
                            delay(1000)
                        } else {
                            _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
                            _binding.mainStatusBroad.setTitle("出现错误")
                            _binding.mainStatusBroad.setSubTitle("账号或密码错误")
                        }
                    }
                }
            } else {
                connecting = false
                toastLong("请先设置账号密码后继续")
                GlobalScope.launch {
                    delay(2000)
                    _binding.mainStatusBroad.post {
                        _binding.mainStatusBroad.changeStateTo(StatusBroad.State.ERROR)
                        _binding.mainStatusBroad.setTitle("出现错误")
                        _binding.mainStatusBroad.setSubTitle("未设置正确的账号")
                    }
                    delay(500)
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
            setOnClickListener {
                jump(AccountActivity::class.java)
            }
        })
        menuRoot.addView(MainMenuItem(this).apply {
            setTitle("配置应用")
            setIcon(resources.getDrawable(R.drawable.ic_apps, null))
            setOnClickListener {
                jump(AppsActivity::class.java)
            }
        })
        menuRoot.addView(MainMenuItem(this).apply {
            setTitle("关于")
            setIcon(resources.getDrawable(R.drawable.ic_about, null))
            //TODO: 弹出关于界面
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
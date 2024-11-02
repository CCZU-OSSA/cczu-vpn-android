package io.github.cczuossa.vpn.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import io.github.cczuossa.vpn.service.ServiceStater
import io.github.cczuossa.vpn.utils.ViewUtils.getStatusBarHeight
import io.github.cczuossa.vpn.utils.ViewUtils.isDarkMode
import io.github.cczuossa.vpn.utils.log
import io.github.cczuossa.vpn.view.StatusBroad
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    private lateinit var _binding: ActivityMainBinding
    lateinit var mActivityResult: ActivityResultLauncher<Intent>
    lateinit var mPermissionResult: ActivityResultLauncher<Array<String>>

    val REQUEST_PERMISSIONS = arrayListOf(
        Manifest.permission.INTERNET,// 基础网络权限
        Manifest.permission.ACCESS_NETWORK_STATE,// 基础网络状态检查权限
        Manifest.permission.ACCESS_WIFI_STATE// 基础wifi状态检查权限
    )

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
                    // TODO: 弹一个弹窗提醒无法继续使用
                    "has permission was false".log()
                    return@registerForActivityResult
                }

            }
            //ServiceStater.start(this)
            // TODO: 检查账号是否设置，进行登录验证
            "check account".log()

            GlobalScope.launch {
                "try login".log()
                WebVpnClient("2200060309", "@lliiooll.com11").login()
            }

        }
    }


    private fun initView() {
        // 设置状态面板(默认StatusBroad.State.STOP)
        val statusBroad = findViewById<StatusBroad>(R.id.main_status_broad)
        statusBroad.setOnClickListener {
            // 尝试检查权限
            mPermissionResult.launch(REQUEST_PERMISSIONS.toTypedArray())
        }
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
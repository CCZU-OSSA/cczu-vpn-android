package io.github.cczuossa.vpn.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.zackratos.ultimatebarx.ultimatebarx.navigationBar
import com.zackratos.ultimatebarx.ultimatebarx.statusBar
import io.github.cczuossa.vpn.CCZUVpnAndroid
import io.github.cczuossa.vpn.databinding.ActivityAppsBinding
import io.github.cczuossa.vpn.ui.adapter.AppAdapter
import io.github.cczuossa.vpn.utils.ConfigUtils
import io.github.cczuossa.vpn.utils.ViewUtils.getStatusBarHeight
import io.github.cczuossa.vpn.utils.ViewUtils.isDarkMode
import io.github.cczuossa.vpn.utils.toastLong

class AppsActivity : AppCompatActivity() {


    private lateinit var _binding: ActivityAppsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAppsBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        // 设置状态栏
        immersionStatusBar()
        // 设置导航栏
        immersionNavigationBar()
        // 初始化view
        initView()

    }


    private fun initView() {
        _binding.appsBack.setOnClickListener { finish() }
        val adapter = AppAdapter(
            packageManager.getInstalledApplications(0).filter { it.packageName != CCZUVpnAndroid.APP.packageName })
        adapter.selects.addAll(ConfigUtils.list("apps"))
        _binding.appsList.adapter = adapter
        _binding.appsList.layoutManager = LinearLayoutManager(this)
        _binding.appsAll.setOnCheckedChangeListener { view, isChecked ->
            if (isChecked) adapter.selectAll() else adapter.unselectAll()
        }
        _binding.appsSave.setOnClickListener {
            ConfigUtils.set("apps", adapter.selects)
            toastLong("保存完毕")
        }
        _binding.appsSearch.setOnClickListener {
            //TODO: 弹出搜索框
        }
    }

    private fun immersionNavigationBar() {
        navigationBar {
            fitWindow = false
            transparent()
            light = !isDarkMode(this@AppsActivity)
        }
    }

    private fun immersionStatusBar() {
        val statusBarHeight = getStatusBarHeight(this.resources)
        // 添加一个和状态栏等高的占位符
        _binding.appsRoot.addView(
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
            light = !isDarkMode(this@AppsActivity)
        }
    }


}
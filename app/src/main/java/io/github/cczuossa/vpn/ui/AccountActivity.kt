package io.github.cczuossa.vpn.ui

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.zackratos.ultimatebarx.ultimatebarx.navigationBar
import com.zackratos.ultimatebarx.ultimatebarx.statusBar
import io.github.cczuossa.vpn.databinding.ActivityAccountBinding
import io.github.cczuossa.vpn.utils.ConfigUtils
import io.github.cczuossa.vpn.utils.ViewUtils.getStatusBarHeight
import io.github.cczuossa.vpn.utils.ViewUtils.isDarkMode
import io.github.cczuossa.vpn.utils.toastLong

class AccountActivity : AppCompatActivity() {


    private lateinit var _binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(_binding.root)
        // 设置状态栏
        immersionStatusBar()
        // 设置导航栏
        immersionNavigationBar()
        // 初始化view
        initView()

    }


    private fun initView() {
        _binding.accountBack.setOnClickListener { finish() }
        _binding.accountSave.setOnClickListener {
            if (_binding.accountEditUser.text.toString().isBlank() || _binding.accountEditPass.text.toString()
                    .isBlank()
            ) {
                toastLong("账号和密码不能为空")
            } else {
                ConfigUtils.set("user", _binding.accountEditUser.text.toString())
                ConfigUtils.set("pass", _binding.accountEditPass.text.toString())
                toastLong("保存成功")
            }
        }

    }

    private fun immersionNavigationBar() {
        navigationBar {
            fitWindow = false
            transparent()
            light = !isDarkMode(this@AccountActivity)
        }
    }

    private fun immersionStatusBar() {
        val statusBarHeight = getStatusBarHeight(this.resources)
        // 添加一个和状态栏等高的占位符
        _binding.accountRoot.addView(
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
            light = !isDarkMode(this@AccountActivity)
        }
    }


}
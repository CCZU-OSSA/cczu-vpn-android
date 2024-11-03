package io.github.cczuossa.vpn.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import io.github.cczuossa.vpn.R
import io.github.cczuossa.vpn.utils.addOnAnimationEndListener

class StatusBroad : LinearLayout {

    private var icon: LottieAnimationView
    private var title: TextSwitcher
    private var subTitle: TextSwitcher

    fun setTitle(text: CharSequence) {
        this.title.setText(text)
    }

    fun setSubTitle(text: CharSequence) {
        this.subTitle.setText(text)
    }

    fun changeStateTo(state: State) {
        when (state) {
            State.START -> {

            }

            State.STOP -> {

            }

            State.CONNECTING -> {

                icon.setAnimation("lottie/stop2loading.json")
                icon.repeatCount = 0
                icon.speed = 2f
                icon.addOnAnimationEndListener {
                    // 结束后循环加载动画
                    icon.speed = 1.5f
                    icon.setAnimation("lottie/loading.json")
                    icon.repeatCount = ValueAnimator.INFINITE
                    icon.playAnimation()
                }
                icon.playAnimation()
                setTitle("连接中...")
                setSubTitle("尝试验证账号信息")
            }

            State.ERROR -> {

            }
        }
    }


    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        LayoutInflater.from(context).inflate(R.layout.status_broad, this, true)
        this.title = findViewById(R.id.status_broad_title)
        this.subTitle = findViewById(R.id.status_broad_subtitle)
        this.icon = findViewById(R.id.status_broad_icon)
        this.title.setFactory { TextView(context) }
        this.subTitle.setFactory { TextView(context) }
        this.title.setText("未连接")
        this.subTitle.setText("点击此处连接")
        this.title.setInAnimation(context, R.anim.fade_in)
        this.title.setOutAnimation(context, R.anim.fade_out)
        this.subTitle.setInAnimation(context, R.anim.fade_in)
        this.subTitle.setOutAnimation(context, R.anim.fade_out)

    }

    enum class State {
        START,// 已启动
        STOP,// 已停止
        CONNECTING,// 连接中
        ERROR,// 出现错误
    }
}
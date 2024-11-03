package io.github.cczuossa.vpn.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextSwitcher
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import io.github.cczuossa.vpn.R
import io.github.cczuossa.vpn.utils.addOnAnimationEndListener
import io.github.cczuossa.vpn.utils.addOnAnimationRepeatistener
import io.github.cczuossa.vpn.utils.log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StatusBroad : LinearLayout {

    private var icon: LottieAnimationView
    private var title: TextSwitcher
    private var subTitle: TextSwitcher
    private var lastState = State.STOP

    fun setTitle(text: CharSequence) {
        this.title.setText(text)
    }

    fun setSubTitle(text: CharSequence) {
        this.subTitle.setText(text)
    }

    fun setIcon(icon: Drawable) {
        this.icon.setImageDrawable(icon)
    }

    fun changeStateTo(state: State) {
        "change $lastState to $state".log()
        icon.removeAllAnimatorListeners()
        when (state) {

            State.START -> {}
            State.STOP -> {}
            State.CONNECTING -> {
                icon.repeatCount = 0
                icon.addOnAnimationEndListener {
                    // 结束后循环加载动画
                    icon.speed = 1.5f
                    icon.repeatCount = ValueAnimator.INFINITE
                    icon.setAnimation("lottie/loading.json")
                    icon.playAnimation()
                    icon.removeAnimatorListener(it)
                }
                setTitle("连接中...")
                setSubTitle("尝试验证账号信息")
                when (lastState) {
                    State.CONNECTING -> {

                    }

                    State.START -> {

                    }

                    State.STOP -> {
                        icon.speed = 2f
                        icon.setAnimation("lottie/stop2loading.json")
                    }

                    State.ERROR -> {
                        icon.setAnimation("lottie/loading2error.json")
                        icon.speed = -2f
                    }
                }
                icon.playAnimation()


            }

            State.ERROR -> {
                icon.repeatCount = 0
                icon.speed = 2f
                when (lastState) {
                    State.CONNECTING -> {
                        icon.setAnimation("lottie/loading2error.json")
                    }

                    State.START -> {
                        //TODO: icon.setAnimation("lottie/start2error.json")
                    }

                    State.STOP -> {

                    }

                    State.ERROR -> {
                    }
                }
                icon.playAnimation()
            }
        }

        lastState = state
    }

    fun setState(start: StatusBroad.State) {
        when (start) {
            State.START -> setIcon(resources.getDrawable(R.drawable.ic_check, null))
            State.STOP -> resources.getDrawable(R.drawable.ic_stop, null)
            State.CONNECTING -> {}
            State.ERROR -> resources.getDrawable(R.drawable.ic_error, null)
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
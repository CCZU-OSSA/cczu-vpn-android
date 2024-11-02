package io.github.cczuossa.vpn.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.cczuossa.vpn.R

class StatusBroad : LinearLayout {

    private var icon: ImageView
    private var title: TextView
    private var subTitle: TextView

    fun setTitle(text: CharSequence) {
        this.title.text = text
    }

    fun setSubTitle(text: CharSequence) {
        this.subTitle.text = text
    }

    fun changeStateTo(state: State) {
        when (state) {
            State.START -> {

            }

            State.STOP -> {

            }

            State.CONNECTING -> {

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
    }

    enum class State {
        START,// 已启动
        STOP,// 已停止
        CONNECTING,// 连接中
        ERROR,// 出现错误
    }
}
package io.github.cczuossa.vpn.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.cczuossa.vpn.R

class MainMenu : LinearLayout {

    private var icon: ImageView
    private var title: TextView

    fun setTitle(text: CharSequence) {
        this.title.text = text
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
        LayoutInflater.from(context).inflate(R.layout.menu_item, this, true)
        this.title = findViewById(R.id.menu_item_title)
        this.icon = findViewById(R.id.menu_item_icon)
    }

}
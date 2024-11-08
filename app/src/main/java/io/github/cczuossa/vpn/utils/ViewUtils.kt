package io.github.cczuossa.vpn.utils

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView

object ViewUtils {
    @JvmStatic
    fun getStatusBarHeight(resources: Resources): Int {
        val id = resources.getIdentifier("status_bar_height", "dimen", "android")

        return if (id > 0) resources.getDimensionPixelSize(id) else 0
    }

    @JvmStatic
    fun isDarkMode(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.getSystemService(UiModeManager::class.java).nightMode == UiModeManager.MODE_NIGHT_YES
        } else false
    }
}

fun Int.dp2px(resources: Resources): Int {
    return Math.round(resources.displayMetrics.density * this)
}

fun Context.toastLong(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

fun Context.jump(clazz: Class<*>) {
    startActivity(Intent(this, clazz))
}

fun LottieAnimationView.addOnAnimationEndListener(onEnd: (listener: AnimatorListener) -> Unit) {
    val animatorListener = object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {
            onEnd.invoke(this)
        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {

        }

    }
    this.addAnimatorListener(animatorListener)
}
fun LottieAnimationView.addOnAnimationRepeatistener(onEnd: (listener: AnimatorListener) -> Unit) {
    val animatorListener = object : AnimatorListener {
        override fun onAnimationStart(animation: Animator) {

        }

        override fun onAnimationEnd(animation: Animator) {

        }

        override fun onAnimationCancel(animation: Animator) {

        }

        override fun onAnimationRepeat(animation: Animator) {
            onEnd.invoke(this)
        }

    }
    this.addAnimatorListener(animatorListener)
}
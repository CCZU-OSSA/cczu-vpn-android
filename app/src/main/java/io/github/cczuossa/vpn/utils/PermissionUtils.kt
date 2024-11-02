package io.github.cczuossa.vpn.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.VpnService
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.cczuossa.vpn.CCZUVpnAndroid
import io.github.cczuossa.vpn.data.PermissionResult
import kotlin.random.Random

object PermissionUtils {

    private val permissionCallbacks = hashMapOf<Int, (results: MutableList<PermissionResult>) -> Unit>()

    fun requestPermission(activity: Activity, requests: List<String>, code: Int) {
        val permissions = arrayListOf<String>()
        requests.forEach {
            if (ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED) {
                permissions.add(it)
            }
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), code)
        }
    }

    fun callPermissionResult(code: Int, results: ArrayList<PermissionResult>) {
        if (permissionCallbacks.containsKey(code)) {
            permissionCallbacks[code]?.invoke(results)
        }
    }

    fun registerPermissionCallback(code: Int, callback: (results: MutableList<PermissionResult>) -> Unit) {
        permissionCallbacks[code] = callback
    }

    fun prepare(activity: AppCompatActivity, callback: (result: Boolean, data: Intent?) -> Unit) {
        VpnService.prepare(CCZUVpnAndroid.APP)
            ?.let { intent ->
                activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    callback.invoke(it.resultCode == RESULT_OK, it.data)
                }.launch(intent)
            } ?: callback.invoke(true, null)
    }

}


fun List<String>.requestPermissions(
    activity: Activity,
    callback: (results: MutableList<PermissionResult>) -> Unit
) {
    val code = Random.nextInt()
    PermissionUtils.registerPermissionCallback(code, callback)
    PermissionUtils.requestPermission(activity, this, code)
}

fun AppCompatActivity.prepare(callback: (result: Boolean, data: Intent?) -> Unit) {
    PermissionUtils.prepare(this, callback)
}



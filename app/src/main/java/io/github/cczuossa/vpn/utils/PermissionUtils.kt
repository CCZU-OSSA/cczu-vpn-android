package io.github.cczuossa.vpn.utils

import android.net.VpnService
import io.github.cczuossa.vpn.CCZUVpnAndroid
import io.github.cczuossa.vpn.service.ServiceStater
import io.github.cczuossa.vpn.ui.MainActivity

object PermissionUtils {

    fun prepare(activity: MainActivity) {
        VpnService.prepare(CCZUVpnAndroid.APP)
            ?.let { intent ->
                activity.mActivityResult.launch(intent)
            } ?: ServiceStater.prepare(activity, true)
    }


}


fun MainActivity.prepare() {
    PermissionUtils.prepare(this)
}



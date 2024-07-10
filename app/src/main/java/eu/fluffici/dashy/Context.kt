package eu.fluffici.dashy

import android.content.Context
import eu.fluffici.security.DeviceInfo

fun Context.getDeviceInfo(): DeviceInfo {
    return DeviceInfo(this)
}
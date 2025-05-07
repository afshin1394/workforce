package irancell.nwg.wfm

import android.content.Context
import android.os.Build
import java.io.File
import java.io.IOException
import java.net.Socket

actual fun isRootedOrEmulator(): Boolean {
    return isRooted() || isEmulator()
}

private fun isRooted(): Boolean {
    // Check for root binaries
    val rootBinaries = listOf(
        "/system/xbin/su",
        "/system/bin/su",
        "/system/app/Superuser.apk",
        "/system/bin/.ext/.su"
    )
    val isRootBinaryPresent = rootBinaries.any { File(it).exists() }

    // Check for dangerous properties
    val dangerousProperties = mapOf(
        "ro.debuggable" to "1",
        "ro.secure" to "0"
    )
    val isDangerousPropertySet = dangerousProperties.any { (key, value) ->
        try {
            val prop = Runtime.getRuntime().exec("getprop $key").inputStream.bufferedReader()
                .use { it.readText().trim() }
            prop == value
        } catch (e: Exception) {
            false
        }
    }

    return isRootBinaryPresent || isDangerousPropertySet
}



private fun isEmulator(): Boolean {
    return Build.MANUFACTURER.contains("Genymotion")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.toLowerCase().contains("droid4x")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.HARDWARE == "goldfish"
            || Build.HARDWARE == "vbox86"
            || Build.HARDWARE.toLowerCase().contains("nox")
            || Build.FINGERPRINT.startsWith("generic")
            || Build.PRODUCT == "sdk"
            || Build.PRODUCT == "google_sdk"
            || Build.PRODUCT == "sdk_x86"
            || Build.PRODUCT == "vbox86p"
            || Build.PRODUCT.toLowerCase().contains("nox")
            || Build.BOARD.toLowerCase().contains("nox")

            || Build.MODEL.toLowerCase().contains("G011A")
            || Build.MANUFACTURER.toLowerCase().contains("google")
            || Build.PRODUCT.toLowerCase().contains("G011A")
            || Build.HARDWARE.toLowerCase().contains("intel")
            || Build.BRAND.toLowerCase().contains("google")
            || Build.DEVICE.toLowerCase().contains("G011A")
            || Build.BOARD.toLowerCase().contains("msm8998")
            || (Build.BRAND.startsWith("generic") &&    Build.DEVICE.startsWith("generic"))
}

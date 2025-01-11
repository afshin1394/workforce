package irancell.nwg.wfm

import android.os.Build
import java.io.File

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
    val buildProps = listOf(
        Build.FINGERPRINT,
        Build.MODEL,
        Build.MANUFACTURER,
        Build.BRAND,
        Build.HARDWARE,
        Build.PRODUCT,
        Build.DEVICE
    )
    return buildProps.any {
        it.contains("generic", ignoreCase = true) ||
                it.contains("emulator", ignoreCase = true) ||
                it.contains("sdk", ignoreCase = true) ||
                it.contains("x86", ignoreCase = true) ||
                it.contains("goldfish", ignoreCase = true) ||
                it.contains("ranchu", ignoreCase = true)
    }
}

package irancell.nwg.wfm

import android.content.Context
import android.os.Build
import android.content.pm.PackageManager

actual class DeviceInfo() {

    actual companion object {

        actual fun getPlatformName(): String {
            return "Android"
        }

        actual fun getOSVersion(): String {
            return Build.VERSION.RELEASE ?: "Unknown"
        }

        actual fun getDeviceModel(): String {
            return Build.MODEL ?: "Unknown"
        }

        actual fun getAppVersionName(): String {
            return try {
                val packageInfo =  (provideAppContext() as Context).packageManager.getPackageInfo( (provideAppContext() as Context).packageName, 0)
                packageInfo.versionName ?: "Unknown"
            } catch (e: PackageManager.NameNotFoundException) {
                "Unknown"
            }
        }

        actual fun getAppVersionCode(): String {
            return try {
                val packageInfo =  (provideAppContext() as Context).packageManager.getPackageInfo( (provideAppContext() as Context).packageName, 0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toString()
                } else {
                    TODO("VERSION.SDK_INT < P")
                }
            } catch (e: PackageManager.NameNotFoundException) {
                "Unknown"
            }
        }

    }


}
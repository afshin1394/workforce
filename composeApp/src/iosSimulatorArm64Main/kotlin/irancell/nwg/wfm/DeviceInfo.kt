package irancell.nwg.wfm

actual class DeviceInfo {


    actual companion object {

        actual fun getPlatformName(): String {
            return UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
        }
        actual fun getOSVersion(): String {
            return UIDevice.currentDevice.systemVersion
        }

        actual fun getDeviceModel(): String {
            return UIDevice.currentDevice.model
        }

        actual fun getAppVersionName(): String {
            return NSBundle.mainBundle.infoDictionary?["CFBundleShortVersionString"] as? String ?: "Unknown"
        }

        actual fun getAppVersionCode(): String {
            return NSBundle.mainBundle.infoDictionary?["CFBundleVersion"] as? String ?: "Unknown"
        }

    }
}
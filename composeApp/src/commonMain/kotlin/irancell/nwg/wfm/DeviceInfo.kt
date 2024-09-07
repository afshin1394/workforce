package irancell.nwg.wfm

expect class DeviceInfo {

    companion object {

        fun getPlatformName(): String
        fun getOSVersion(): String
        fun getDeviceModel(): String
        fun getAppVersionName(): String
        fun getAppVersionCode(): String
    }
}
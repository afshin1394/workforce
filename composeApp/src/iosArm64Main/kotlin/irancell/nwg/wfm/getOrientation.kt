package irancell.nwg.wfm
import platform.UIKit.UIDevice
actual fun getOrientation(): String {
    return when (UIDevice.currentDevice.orientation) {
        UIDeviceOrientation.UIDeviceOrientationLandscapeLeft,
        UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> "Landscape"
        UIDeviceOrientation.UIDeviceOrientationPortrait,
        UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> "Portrait"
        else -> "Unknown"
    }
}
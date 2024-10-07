package irancell.nwg.wfm

import com.linecorp.abc.location.ABCLocation

actual fun openAppSettings(){

}
actual fun provideAppContext() : Any{
    return UIApplication.sharedApplication.delegate  // Assuming YourAndroidApplication is your Application class
}

actual fun provideLifeCycleOwner() : Any{

}

actual fun nullIfAllPropertiesNull(){

}

actual fun <T : Any> T.nullIfAllPropertiesNull(): T? {
    TODO("Not yet implemented")
}

actual fun openVpnSettings() {
}

actual fun getPlatform(): Platform {
    TODO("Not yet implemented")
}

actual fun openInternetSettings() {
}

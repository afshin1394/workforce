import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    ovveride val os : String = "IOS"
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val versionName : String =   platform.Foundation.NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion")
}

actual fun getPlatform(): Platform = IOSPlatform()


actual fun provideAppContext(): Any {
    // You can return the application context or any context you need
    return UIApplication.sharedApplication.delegate  // Assuming YourAndroidApplication is your Application class
}


actual fun provideLifeCycleOwner() : Any{

}



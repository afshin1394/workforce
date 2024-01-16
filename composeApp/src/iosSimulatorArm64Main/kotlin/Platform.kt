import com.linecorp.abc.location.ABCLocation

actual fun openAppSettings(){

}
actual fun provideAppContext() : Any{
    return UIApplication.sharedApplication.delegate  // Assuming YourAndroidApplication is your Application class
}

actual fun provideLifeCycleOwner() : Any{

}



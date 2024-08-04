package irancell.nwg.wfm

interface Platform {
    val os : String
    val name: String
    val versionName: String
}

expect fun getPlatform(): Platform
expect fun openAppSettings()
expect fun provideAppContext(): Any

expect fun provideLifeCycleOwner(): Any


expect fun <T : Any> T.nullIfAllPropertiesNull(): T?


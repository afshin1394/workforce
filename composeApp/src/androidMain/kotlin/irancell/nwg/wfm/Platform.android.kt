package irancell.nwg.wfm

import android.os.Build
import dev.icerock.moko.graphics.BuildConfig

class AndroidPlatform : Platform {
    override val os: String = "Android"
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val versionName : String = ""
}

actual fun getPlatform(): Platform = AndroidPlatform()
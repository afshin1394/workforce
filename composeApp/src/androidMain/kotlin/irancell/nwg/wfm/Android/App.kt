package irancell.nwg.wfm.Android

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
//import com.mapbox.common.MapboxOptions
import dev.icerock.moko.graphics.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.stopKoin
import presentation.nav.featurePostsScreenModule

class App : Application() {

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
//        MapboxOptions.accessToken = "pk.eyJ1IjoiYWZzaGluMTk5NDEzNzMiLCJhIjoiY2tkeWRoOW13MWphdTJ0c2c2MDhudHRhdCJ9.qJ_zRtgo_X67WNtNxaiB4A"

        ScreenRegistry {
            featurePostsScreenModule()
        }
        INSTANCE = this

        if (BuildConfig.DEBUG) {

            Napier.base(DebugAntilog())
        }
    }
}
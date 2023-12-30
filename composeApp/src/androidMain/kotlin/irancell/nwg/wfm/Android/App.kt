package irancell.nwg.wfm.Android

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import dev.icerock.moko.graphics.BuildConfig
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import presentation.nav.featurePostsScreenModule

class App : Application() {

    companion object {
        lateinit var INSTANCE: App
    }

    override fun onCreate() {
        super.onCreate()
        ScreenRegistry {
            featurePostsScreenModule()
        }
        INSTANCE = this

        if (BuildConfig.DEBUG) {

            Napier.base(DebugAntilog())
        }
    }

}
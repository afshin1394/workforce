package irancell.nwg.wfm

import App
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import cafe.adriel.voyager.navigator.Navigator
import data.GeneralLocationRepositoryImpl
import dev.icerock.moko.resources.desc.StringDesc
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import presentation.screens.splash.compose.SplashScreen
import utils.Language
import utils.SelectLanguage
import utils.isRunningGPS
import java.util.Locale

class MainActivity : FragmentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        updateConfig(this)
        super.attachBaseContext(newBase)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if ( getSharedPref().getString(Language)=="fa"){
            getSharedPref().put(Language, "en")
        }else{
            getSharedPref().put(Language, "fa")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
          App()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopKoin()
    }

}


@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
package irancell.nwg.wfm

import App
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import irancell.nwg.wfm.GPS.Companion.enableGps
import irancell.nwg.wfm.GPS.Companion.registerGps

import org.koin.core.context.stopKoin
import utils.Language
import java.util.Locale

class MainActivity : FragmentActivity() {
    override fun attachBaseContext(newBase: Context?) {

        if(getSharedPref().getString(Language)==""||getSharedPref().getString(Language)==null){
            getSharedPref().put(Language, Locale.getDefault().language)
        }
        updateConfig(this)
        super.attachBaseContext(newBase)
    }



    /*    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if ( getSharedPref().getString(Language)=="fa"){
            getSharedPref().put(Language, "en")
        }else{
            getSharedPref().put(Language, "fa")
        }

    }*/



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
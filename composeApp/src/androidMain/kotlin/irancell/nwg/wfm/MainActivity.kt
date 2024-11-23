package irancell.nwg.wfm

import App
import android.content.Context
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import io.sentry.Sentry
import org.koin.core.context.stopKoin
import utils.Language
import utils.ModeApp
import utils.ORIENTATION
import utils.UpdateTaskListTypes
import utils.UpdateType
import java.util.Locale

class MainActivity : FragmentActivity() {


    override fun attachBaseContext(newBase: Context?) {
        if(getSharedPref().getString(UpdateType)==""||getSharedPref().getString(UpdateType)==null){
            getSharedPref().put(UpdateType, UpdateTaskListTypes.Auto)
        }

        if(getSharedPref().getString(ModeApp)==""||getSharedPref().getString(ModeApp)==null){
            getSharedPref().put(ModeApp, "on")
        }
        if(getSharedPref().getString(Language)==""||getSharedPref().getString(Language)==null){
            getSharedPref().put(Language, Locale.getDefault().language)
        }
        updateConfig(this)
        super.attachBaseContext(newBase)
    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val packageInfo: PackageInfo =
            (provideAppContext() as Context).packageManager.getPackageInfo((provideAppContext() as Context).packageName, 0)

        Sentry.init { options ->
            options.dsn = "https://c38def8cf951033c69c3c1cdfe6ec3e7@o4505880661065728.ingest.us.sentry.io/4507055751036928"
            options.environment = "production"
            options.release = packageInfo.versionName // Set the release version
        }
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
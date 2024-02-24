package irancell.nwg.wfm

import App
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.context.stopKoin
import utils.Language

class MainActivity : FragmentActivity() {
    companion object {
        const val LOCATION_SETTING_REQUEST = 999
    }
    val locationServiceEnabled = MutableStateFlow(false)

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


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
                App()

//            val locationServiceState = locationServiceEnabled.collectAsState()
//
//            val receiver = ChangeGpsReceiver{
//                locationServiceEnabled.update { false }
//                stopKoin()
//
//            }
//            registerReceiver(receiver,  IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION))




//            if (locationServiceState.value){
//                App()
//            }else{
//                val settingResultRequest = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.StartIntentSenderForResult()
//                ) { activityResult ->
//                    if (activityResult.resultCode == RESULT_OK)
//                        locationServiceEnabled.update { true }
//                    else {
//                        locationServiceEnabled.update { false }
//                    }
//                }
//                checkLocationSetting( this, onDisabled =  {
//                    settingResultRequest.launch(it)
//
//                }, onEnabled =  {
//                    locationServiceEnabled.update { true }
//
//                })
//            }

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
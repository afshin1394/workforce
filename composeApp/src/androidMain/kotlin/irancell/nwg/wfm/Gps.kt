package irancell.nwg.wfm

import android.app.Activity
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.location.LocationManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.context.stopKoin

actual class GPS {
    actual companion object {

        fun checkLocationSetting(
            context: Context,
            onDisabled: (IntentSenderRequest) -> Unit,
            onEnabled: () -> Unit
        ) {

            val locationRequest = LocationRequest.create().apply {
                interval = 1000
                fastestInterval = 1000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
                .Builder()
                .addLocationRequest(locationRequest)

            val gpsSettingTask: Task<LocationSettingsResponse> =
                client.checkLocationSettings(builder.build())

            gpsSettingTask.addOnSuccessListener { onEnabled() }
            gpsSettingTask.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest
                            .Builder(exception.resolution)
                            .build()
                        onDisabled(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // ignore here
                    }
                }
            }
        }
        actual fun registerGps(context: Any,onChange : () -> Unit){
            val receiver = ChangeGpsReceiver {
                onChange()
            }
            (context as Context).registerReceiver(
                receiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        }

        @Composable
        actual fun enableGps(context: Any, enabled: () -> Unit,disable : () -> Unit) {

            Napier.log(LogLevel.ASSERT,"GPS", message = "enableGPS")

                val settingResultRequest = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartIntentSenderForResult()
                ) { activityResult ->
                    if (activityResult.resultCode == FragmentActivity.RESULT_OK)
                        enabled()
                    else
                        disable()
                }

                checkLocationSetting(context as Context, onDisabled = {
                    settingResultRequest.launch(it)

                }, onEnabled = {
                    enabled()
                })

        }
    }
}
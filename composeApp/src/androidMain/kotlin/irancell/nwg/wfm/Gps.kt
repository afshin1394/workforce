package irancell.nwg.wfm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.location.LocationManager
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import presentation.components.CustomDialog
import presentation.theme.surfaceBrandDefault

actual class GPS {

    actual companion object {
        actual fun registerGps(context: Any,onChange : (boolean:Boolean) -> Unit){
            val receiver = ChangeGpsReceiver()
            receiver.initChangeReceiver(object  : IChangeGpsReceiver{
                override fun locationOn() {
                    onChange(true)
                }

                override fun locationOff() {
                    onChange(false)

                }

            })
            (context as Context).registerReceiver(
                receiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        }

        actual fun getLocationsState() : Boolean{
            val locationManager =
                (provideAppContext() as Context).getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }


        actual fun enableGpsDialog(context: Any) {

            Napier.log(LogLevel.ASSERT,"GPSPermission", message = "enableGPS")
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            (context as Context).startActivity(intent)

//            CustomDialog(true, message =  MR.strings.disc_gps_permission, title = MR.strings.GPS_Permission,
//                onConfirm = {
//                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    (context as Context).startActivity(intent)
//
//                }, onDismiss = {}, titleButton = MR.strings.enable)

        }
    }


}
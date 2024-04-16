package irancell.nwg.wfm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

interface IChangeGpsReceiver {
    fun locationOn()
    fun locationOff()
}

class ChangeGpsReceiver() : BroadcastReceiver() {
    var iChangeGpsReceiver: IChangeGpsReceiver? = null
    fun initChangeReceiver(iChangeGpsReceiver: IChangeGpsReceiver) {
        this.iChangeGpsReceiver = iChangeGpsReceiver
    }

    override fun onReceive(context: Context?, intent: Intent) {

        if (intent.action == (LocationManager.PROVIDERS_CHANGED_ACTION)) {
            val locationManager =
                context!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (gpsEnabled)
                iChangeGpsReceiver?.locationOn()
            else
                iChangeGpsReceiver?.locationOff()

        }

    }
}
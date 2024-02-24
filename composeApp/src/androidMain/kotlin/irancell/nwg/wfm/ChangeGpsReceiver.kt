package irancell.nwg.wfm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager


class ChangeGpsReceiver(private val locationServiceChanged : () ->Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent : Intent) {

        if (intent.action == (LocationManager.PROVIDERS_CHANGED_ACTION)) {

            val replyIntent = Intent(LocationManager.PROVIDERS_CHANGED_ACTION)
            locationServiceChanged()

//            context?.sendBroadcast(replyIntent)

        }

    }
}
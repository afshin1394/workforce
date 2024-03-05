package irancell.nwg.wfm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.LocationManager

interface IChangeGpsReceiver{
    fun onReceiveAction()
}

class ChangeGpsReceiver() : BroadcastReceiver() {
    var iChangeGpsReceiver: IChangeGpsReceiver? = null
    var changed : Boolean = true
    fun initChangeReceiver(iChangeGpsReceiver: IChangeGpsReceiver){
        this.iChangeGpsReceiver = iChangeGpsReceiver
    }
    override fun onReceive(context: Context?, intent : Intent) {

        if (intent.action == (LocationManager.PROVIDERS_CHANGED_ACTION)) {

            iChangeGpsReceiver?.let {
                changed = if (changed) {
                    it.onReceiveAction()
                    false
                } else
                    true
            }

        }

    }
}
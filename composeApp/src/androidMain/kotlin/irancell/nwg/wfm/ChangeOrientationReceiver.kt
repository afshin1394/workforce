package irancell.nwg.wfm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration

interface IChangeOrientationReceiver {
    fun landscape()
    fun portrait()
}
class ChangeOrientationReceiver() : BroadcastReceiver() {
    var iChangeOrientationReceiver: IChangeOrientationReceiver? = null
    fun initChangeReceiver(iChangeOrientationReceiver: IChangeOrientationReceiver) {
        this.iChangeOrientationReceiver = iChangeOrientationReceiver
    }

    override fun onReceive(context: Context?, intent: Intent) {

        if (intent.action == Intent.ACTION_CONFIGURATION_CHANGED) {
            val orientation = getOrientation()

            if (orientation=="Landscape"){
                iChangeOrientationReceiver?.landscape()
            }else{
                iChangeOrientationReceiver?.portrait()
            }

        }

    }


    private fun getOrientation(): String {
        return if((provideAppContext() as Context).resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            "Landscape"
        } else {
            "Portrait"
        }
    }
}
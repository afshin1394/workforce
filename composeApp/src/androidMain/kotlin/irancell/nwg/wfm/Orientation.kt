package irancell.nwg.wfm

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager


actual  class Orientation {

    actual companion object {
        actual fun orientationState(context: Any, onChange: (state:String) -> Unit) {
            val receiver = ChangeOrientationReceiver()
            receiver.initChangeReceiver(object : IChangeOrientationReceiver {
                override fun landscape() {
                    onChange("landscape")

                }

                override fun portrait() {
                    onChange("portrait")

                }


            })
            (context as Context).registerReceiver(
                receiver,
                IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED)
            )
        }
    }





}
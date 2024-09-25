package irancell.nwg.wfm

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.util.Log


actual fun openInMap(lat: String, long: String) {
    val geoUri = "http://maps.google.com/maps?q=loc:$lat,$long"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(geoUri))

    intent.setFlags(FLAG_ACTIVITY_NEW_TASK)

    val context = provideAppContext() as Context;
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

package irancell.nwg.wfm

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import androidx.compose.runtime.Composable


actual fun openInMap(lat: String, long: String) {
    val uri = "geo:$lat,$long"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
    intent.setFlags(FLAG_ACTIVITY_NEW_TASK)

    val context = provideAppContext() as Context;
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}

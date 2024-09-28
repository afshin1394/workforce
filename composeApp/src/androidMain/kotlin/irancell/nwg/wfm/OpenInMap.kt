package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import java.util.Locale


@SuppressLint("DefaultLocale")
actual fun openInMap(lat: String, long: String) {
    try {
        val uri = java.lang.String.format(
            Locale.ENGLISH,
            "geo:$lat,$long?q=$lat,$long ()"
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK)
        val context = provideAppContext() as Context;

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    } catch (ex: ActivityNotFoundException) {
    }
}

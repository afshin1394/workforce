package irancell.nwg.wfm

import android.net.Uri

actual fun ParseUri(uriString: String): Any {
    return Uri.parse(uriString)
}
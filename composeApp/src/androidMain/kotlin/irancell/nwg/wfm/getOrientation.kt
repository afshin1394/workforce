package irancell.nwg.wfm
import android.content.Context
import android.content.res.Configuration


actual fun getOrientation(): String {
    return if ((provideAppContext() as Context).resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        "Landscape"
    } else {
        "Portrait"
    }
}
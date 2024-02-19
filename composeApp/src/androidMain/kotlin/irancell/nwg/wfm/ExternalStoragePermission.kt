package irancell.nwg.wfm

import android.Manifest
import android.os.Build

actual fun canReadExternalStorage() : Boolean{
       return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
}
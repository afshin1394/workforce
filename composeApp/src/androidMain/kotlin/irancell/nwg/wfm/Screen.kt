package irancell.nwg.wfm

import android.content.Context

actual fun getDpi() : Float{
   return (provideAppContext() as Context) .resources.displayMetrics.density;
}
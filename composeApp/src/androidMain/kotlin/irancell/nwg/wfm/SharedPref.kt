package irancell.nwg.wfm

import android.content.Context

actual fun getSharedPref() : KMMPreference{
  return  KMMPreference(KMMContext.INSTANCE)
}
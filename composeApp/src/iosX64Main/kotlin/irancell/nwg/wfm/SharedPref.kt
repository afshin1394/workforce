package irancell.nwg.wfm

import provideAppContext

actual fun getSharedPref() : KMMPreference{
  return  KMMPreference(KMMContext.INSTANCE)
}
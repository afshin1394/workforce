package irancell.nwg.wfm


actual fun getSharedPref() : KMMPreference{
  return  KMMPreference(KMMContext.INSTANCE)
}
package irancell.nwg.wfm

import android.content.res.Configuration
import android.view.ContextThemeWrapper
import utils.Language
import java.util.Locale

actual fun updateConfig(wrapper: Any) {
    val lang = getSharedPref().getString(Language)
    Locale.setDefault(Locale(lang))
    val configuration = Configuration()
    configuration.setLocale(Locale(lang))
    (wrapper as ContextThemeWrapper).applyOverrideConfiguration(configuration)

}



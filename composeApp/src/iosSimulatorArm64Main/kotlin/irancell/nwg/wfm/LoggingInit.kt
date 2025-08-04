package irancell.nwg.wfm

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

actual fun initializeLogging() {
    Napier.base(DebugAntilog())
}
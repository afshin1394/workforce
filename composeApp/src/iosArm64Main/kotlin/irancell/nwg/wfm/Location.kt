package irancell.nwg.wfm

import irancell.nwg.wfm.db.GeneralLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.wasm.jsinterop.Object

actual class Location actual constructor(){
    actual companion object {
        actual fun start(update: (GeneralLocation) -> Unit) {

        }

        actual fun stop() {

        }
    }
}
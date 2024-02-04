package irancell.nwg.wfm

import irancell.nwg.wfm.db.GeneralLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

actual class Location actual constructor(){
    actual companion object {

        actual  fun start(update : (GeneralLocationEntity) -> Unit) {
        }

        actual  fun stop() {
        }
    }

}
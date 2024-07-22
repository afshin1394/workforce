package irancell.nwg.wfm


import database.entity.GeneralLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import utils.getCurrentDate

actual class Location actual constructor(){
    actual companion object {

        actual  fun start(update : (GeneralLocationEntity) -> Unit) {
        }

        actual  fun stop() {
        }

        actual fun getLastLocation() : GeneralLocationEntity{
            return GeneralLocationEntity("","", getCurrentDate(),0)

        }
    }
}
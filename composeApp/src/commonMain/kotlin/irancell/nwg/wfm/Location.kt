package irancell.nwg.wfm

import database.entity.GeneralLocationEntity


expect class Location  (){
    companion object {
         fun start( update:  (GeneralLocationEntity) -> Unit)
         fun stop()

         fun getLastLocation() : GeneralLocationEntity
    }
}

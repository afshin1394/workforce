package irancell.nwg.wfm

import irancell.nwg.wfm.db.GeneralLocationEntity

expect class Location  (){
    companion object {
         fun start( update:  (GeneralLocationEntity) -> Unit)
         fun stop()
    }
}

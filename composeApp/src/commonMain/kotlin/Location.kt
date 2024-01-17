import irancell.nwg.wfm.db.GeneralLocation

expect class Location  (){
    companion object {
        fun start(update: (GeneralLocation) -> Unit)
        fun stop()
    }
}

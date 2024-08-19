package irancell.nwg.wfm

 expect class BackgroundWorker {

    companion object {
        fun start(intervalMillis: Long, action: () -> Unit)
        fun stop()
    }
}
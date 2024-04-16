package irancell.nwg.wfm

expect class BackgroundServiceApp {
    companion object {
       // var isRunning:Boolean
        fun startBackgroundService()
        fun stopBackgroundService()
    }
}
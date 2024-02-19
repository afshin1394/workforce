package irancell.nwg.wfm

expect class CountdownTimer(seconds: Int, listener: TimerListener) {
    fun start()
    fun stop()
}

interface TimerListener {
    fun onTick(secondsLeft: Int)
    fun onFinish()
}
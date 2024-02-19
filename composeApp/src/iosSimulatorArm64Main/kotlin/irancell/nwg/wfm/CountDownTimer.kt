package irancell.nwg.wfm

import platform.Foundation.NSTimer
import platform.Foundation.NSRunLoop
import kotlin.concurrent.schedule

actual class CountdownTimer actual constructor(private val seconds: Int, private val listener: TimerListener) {
    private var timer: NSTimer? = null
    private var currentSeconds = seconds

    actual fun start() {
        timer = NSTimer.scheduledTimerWithTimeInterval(1.0, true) {
            listener.onTick(currentSeconds)
            if (currentSeconds == 0) {
                listener.onFinish()
                stop()
            } else {
                currentSeconds--
            }
        }
        NSRunLoop.currentRunLoop().addTimer(timer, NSRunLoopCommonModes)
    }

    actual fun stop() {
        timer?.invalidate()
        timer = null
    }
}
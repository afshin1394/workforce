package irancell.nwg.wfm

import platform.Foundation.*

actual class BackgroundWorker {
    private var timer: NSTimer? = null

    actual fun start(intervalMillis: Long, action: () -> Unit) {
        timer = NSTimer.scheduledTimerWithTimeInterval(
            intervalMillis / 1000.0,
            true,
            block = { _ -> action() }
        )
    }

    actual fun stop() {
        timer?.invalidate()
        timer = null
    }
}

package irancell.nwg.wfm

import android.os.CountDownTimer

actual class CountdownTimer actual constructor(private val seconds: Int, private val listener: TimerListener) {
    private var timer: CountDownTimer? = null

    actual fun start() {
        timer = object : CountDownTimer((seconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                listener.onTick(secondsLeft)
            }

            override fun onFinish() {
                listener.onFinish()
            }
        }.start()
    }

    actual fun stop() {
        timer?.cancel()
    }
}
package irancell.nwg.wfm
import android.content.Context
import java.util.concurrent.TimeUnit

actual class BackgroundWorker {

    actual companion object{
//
//
//        private var workRequest: PeriodicWorkRequest? = null
//        actual fun start(intervalMillis: Long, action: () -> Unit) {
//            workRequest = PeriodicWorkRequestBuilder<CustomWorker>(
//                intervalMillis, TimeUnit.MILLISECONDS
//            ).build()
//
//            WorkManager.getInstance(provideAppContext() as Context).enqueueUniquePeriodicWork(
//                "PeriodicWork",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                workRequest!!
//            )
//
//            CustomWorker.setAction(action)
//        }
//
//
//        actual fun stop() {
//            workRequest?.let {
//
//                WorkManager.getInstance(provideAppContext() as Context).cancelUniqueWork("PeriodicWork")
//            }
//        }
//
//    }
        actual fun start(intervalMillis: Long, action: () -> Unit) {
        }

        actual fun stop() {
        }


    }

//class CustomWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
//
//    companion object {
//        private var action: (() -> Unit)? = null
//
//        fun setAction(act: () -> Unit) {
//            action = act
//        }
//    }
//
//    override fun doWork(): Result {
//        action?.invoke()
//        return Result.success()
    }
//}

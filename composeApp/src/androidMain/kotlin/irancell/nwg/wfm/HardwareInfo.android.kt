package irancell.nwg.wfm

import android.app.ActivityManager
import android.content.Context
import kotlinx.coroutines.sync.Semaphore

actual fun hardwareInfo(): Semaphore {
    val context = provideAppContext() as Context

    val availableCores = Runtime.getRuntime().availableProcessors()

    val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalRam = memoryInfo.totalMem / (1024 * 1024)

    val semaphoreCount = when {
        totalRam > 4096 && availableCores >= 8 -> 8
        totalRam > 2048 && availableCores >= 4 -> 4
        else -> 2
    }

    val semaphore = Semaphore(semaphoreCount)

    println("Hardware Info: Cores: $availableCores, RAM: $totalRam MB, Semaphore Count: $semaphoreCount")

    return semaphore
}
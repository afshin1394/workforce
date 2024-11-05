package utils

import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.hardwareInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withPermit

suspend fun <T> processInParallel(
    items: List<T>,
    processBlock: suspend (T, Mutex) -> Unit,
    onError: suspend (T, Exception) -> Unit = { _, exception ->
        Napier.log(LogLevel.ASSERT, tag = "exception", message = exception.toString())
    }
) {
    val mutex = Mutex()
    val semaphore = hardwareInfo()
    coroutineScope {
        val deferredProcessing = items.map { item ->
            async(Dispatchers.IO) {
                semaphore.withPermit {
                    try {
                        processBlock(item, mutex)
                    } catch (e: Exception) {
                        onError(item, e)
                    }
                }
            }
        }
        deferredProcessing.awaitAll()
    }
}

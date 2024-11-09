package utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Composable
fun debounceClick(
    debounceTime: Long = 1000L,
    onClick: () -> Unit
): () -> Unit {
    val scope = rememberCoroutineScope()
    val mutex = Mutex()
    var job: Job? = null

    return {
        scope.launch {
            mutex.withLock {
                job?.cancel()
                job = launch {
                    onClick()
                    delay(debounceTime)
                }
            }
        }
    }
}
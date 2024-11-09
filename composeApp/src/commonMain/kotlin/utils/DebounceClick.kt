package utils

import androidx.compose.runtime.*
import kotlinx.coroutines.*

@Composable
fun debounceClick(
    debounceTime: Long = 1000L,
    onClick: () -> Unit
): () -> Unit {
    var isClickAllowed by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    return {
        if (isClickAllowed) {
            onClick()
            isClickAllowed = false
            scope.launch {
                delay(debounceTime)
                isClickAllowed = true
            }
        }
    }
}

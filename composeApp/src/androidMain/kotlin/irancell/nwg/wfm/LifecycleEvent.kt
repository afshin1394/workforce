package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver


@Composable
actual fun OnLifecycleEvent(onEvent: (owner: Any, event: Any) -> Unit) {
    val eventHandler = rememberUpdatedState(onEvent)
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    var executeOne by remember { mutableStateOf(true) }
    var observer :LifecycleEventObserver?=null



    DisposableEffect(lifecycleOwner.value) {
        val lifecycle = lifecycleOwner.value.lifecycle
        if (executeOne) {
            observer = LifecycleEventObserver { owner, event ->
                eventHandler.value(owner, mapEvent(event))
            }
            lifecycle.addObserver(observer!!)
            executeOne = false

        }

        onDispose {
            observer?.let { lifecycle.removeObserver(it) }
        }
    }
}


private fun mapEvent(event: Lifecycle.Event): LifecycleEvent {
    return when (event) {
        Lifecycle.Event.ON_CREATE -> LifecycleEvent.ON_CREATE
        Lifecycle.Event.ON_START -> LifecycleEvent.ON_START
        Lifecycle.Event.ON_RESUME -> LifecycleEvent.ON_RESUME
        Lifecycle.Event.ON_PAUSE -> LifecycleEvent.ON_PAUSE
        Lifecycle.Event.ON_STOP -> LifecycleEvent.ON_STOP
        Lifecycle.Event.ON_DESTROY -> LifecycleEvent.ON_DESTROY
        else -> LifecycleEvent.ON_ANY
    }
}

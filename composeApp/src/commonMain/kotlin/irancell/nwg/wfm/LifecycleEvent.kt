package irancell.nwg.wfm

import androidx.compose.runtime.Composable


enum class LifecycleEvent{
    ON_CREATE,
    ON_START,
    ON_RESUME,
    ON_PAUSE,
    ON_STOP,
    ON_DESTROY,
    ON_ANY

}
@Composable
expect fun OnLifecycleEvent(onEvent: (owner: Any, event: Any) -> Unit)
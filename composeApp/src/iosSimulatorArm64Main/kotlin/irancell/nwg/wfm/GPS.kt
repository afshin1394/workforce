package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

actual class GPS {
    actual companion object {
        @Composable
        actual fun enableGps(context: Any, enabled: (locationState : MutableStateFlow<Boolean>) -> Unit) {
        }


    }
}
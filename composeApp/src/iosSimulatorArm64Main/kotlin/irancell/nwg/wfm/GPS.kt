package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

actual class GPS {
    actual companion object {
        @Composable
        actual fun enableGpsDialog(context: Any) {
        }

        actual fun getLocationsState(): Boolean {
            TODO("Not yet implemented")
        }

        actual fun registerGps(
            context: Any,
            onChange: (boolean: Boolean) -> Unit
        ) {
        }


    }
}
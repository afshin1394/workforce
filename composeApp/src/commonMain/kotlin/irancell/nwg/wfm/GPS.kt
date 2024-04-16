package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

expect class GPS {
    companion object {
        @Composable
        fun enableGpsDialog(context: Any)

        fun getLocationsState(): Boolean
        fun registerGps(context: Any, onChange: (boolean: Boolean) -> Unit)
    }
}
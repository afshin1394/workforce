package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

expect class GPS {
    companion object {
        @Composable
         fun enableGps(context: Any, enabled: () -> Unit,disable : () -> Unit)

         fun registerGps(context: Any,onChange : () -> Unit)
    }
}
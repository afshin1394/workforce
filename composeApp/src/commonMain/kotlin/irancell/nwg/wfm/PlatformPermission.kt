package irancell.nwg.wfm

import androidx.compose.runtime.Composable

@Composable
expect fun checkPermission(granted : () -> Unit, showRational : () -> Unit)
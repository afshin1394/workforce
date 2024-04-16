package irancell.nwg.wfm

import androidx.compose.runtime.Composable

expect class BackButtonHandler {

    companion object {
        @Composable
        fun backPress(  onBackPressed : () -> Unit)
    }
}
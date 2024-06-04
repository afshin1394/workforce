package irancell.nwg.wfm

import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class BackButtonHandler {
    actual companion object {
        @Composable
        actual fun backPress(onBackPressed: () -> Unit) {
            val currentActivity = LocalContext.current as? FragmentActivity
                ?: error("Current context is not a FragmentActivity")

            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
            currentActivity.onBackPressedDispatcher.addCallback(currentActivity, callback)
        }
    }
}
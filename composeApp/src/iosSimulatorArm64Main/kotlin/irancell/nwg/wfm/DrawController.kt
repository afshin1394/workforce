package irancell.nwg.wfm

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.irancell.nwg.wfm.presentation.model.View
import kotlinx.coroutines.CoroutineScope

actual class DrawController {

    actual companion object {
        actual fun trackHistory(
            scope: CoroutineScope,
            trackHistory: (undoCount: Int, redoCount: Int) -> Unit
        ) {
        }

        actual fun saveBitmap(): Boolean {

            return false
        }

        actual fun changeOpacity(value: Float) {

        }

        actual fun changeColor(value: Color) {

        }

        actual fun changeBgColor(value: Color) {

        }

        actual fun changeStrokeWidth(value: Float) {

        }

        actual fun unDo() {

        }

        actual fun reDo() {

        }

        actual fun reset() {
        }

        actual fun updateLatestPath(newPoint: Offset) {
        }

        actual fun insertNewPath(newPoint: Offset) {
        }

        actual fun trackBitmaps(it: Any, coroutineScope: CoroutineScope, onCaptured: (ImageBitmap?, Throwable?) -> Unit) {
            if (it is View) {
            }
        }
        actual fun getColor() : Color{
            return Color.Red
        }

    }
}
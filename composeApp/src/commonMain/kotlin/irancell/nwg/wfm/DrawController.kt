package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.CoroutineScope

expect class DrawController {


    companion object {
        fun trackHistory(
            scope: CoroutineScope,
            trackHistory: (undoCount: Int, redoCount: Int) -> Unit
        )
        fun saveBitmap(): Boolean
        fun changeOpacity(value: Float)
        fun changeColor(value: Color)
        fun changeBgColor(value: Color)
        fun changeStrokeWidth(value: Float)
       fun unDo()
        fun reDo()
        fun reset()
        fun updateLatestPath(newPoint: Offset)
        fun insertNewPath(newPoint: Offset)
        fun trackBitmaps(it: Any, coroutineScope: CoroutineScope, onCaptured: (ImageBitmap?, Throwable?) -> Unit)



    }


}
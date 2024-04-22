package irancell.nwg.wfm

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap


@Composable
actual fun CreateDrawBox(
   imageBitmap: ImageBitmap,
    modifier: Modifier,
    backgroundColor: Color,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit
): Any {

    return Any()
}
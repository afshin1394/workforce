package irancell.nwg.wfm

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap

@Composable
expect fun CreateDrawBox(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier.fillMaxSize(),

    backgroundColor: Color = Color.White,
    bitmapCallback: (ImageBitmap?, Throwable?) -> Unit,
    trackHistory: (undoCount: Int, redoCount: Int) -> Unit = { _, _ -> }
): Any
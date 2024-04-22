package irancell.nwg.wfm

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap

actual  fun  ImageBitmapToBitmap(imageBitmap: ImageBitmap):Any {
    return imageBitmap.asAndroidBitmap()

}
package irancell.nwg.wfm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dev.icerock.moko.resources.compose.painterResource



fun rotateBitmap(bitmap: Bitmap, angle: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

actual fun UriToImageBitmap( uri: Any,angle :Float): Any {
    val context= provideAppContext() as Context

    val bitmap: Bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver,(uri as Uri) ))
    } else {
        context.contentResolver.openInputStream((uri as Uri))?.use {
            BitmapFactory.decodeStream(it)
        } ?: throw IllegalArgumentException("Unable to load bitmap from uri: $uri")
    }

    val rotatedBitmap = rotateBitmap(bitmap, angle)

    return rotatedBitmap.asImageBitmap()
}



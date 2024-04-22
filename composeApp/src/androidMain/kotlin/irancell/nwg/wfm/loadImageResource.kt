package irancell.nwg.wfm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.core.content.ContextCompat



//@Composable
//actual fun loadImageResource(resourceId: Int): Any {
//    val res= R.drawable.download
//    return ImageBitmap.imageResource(res)
//}
/*@Composable
actual fun loadImageResource(resourceId: Int): Any {
    val res=R.drawable.download
    val con= LocalContext.current

    return createBitmapFromDrawable(con,res).asImageBitmap()
}*/


private fun createBitmapFromDrawable(context: Context, drawableResId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableResId)
    val picture = drawableToPicture(drawable)
    return createBitmapFromPicture(picture)
}

private fun drawableToPicture(drawable: Drawable?): Picture {
    val picture = Picture()
    val canvas = picture.beginRecording(drawable?.intrinsicWidth ?: 0, drawable?.intrinsicHeight ?: 0)
    drawable?.draw(canvas)
    picture.endRecording()
    return picture
}
private fun createBitmapFromPicture(picture: Picture): Bitmap {
    val bitmap = Bitmap.createBitmap(
        picture.width,
        picture.height,
        Bitmap.Config.ARGB_8888
    )

    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawPicture(picture)
    return bitmap
}
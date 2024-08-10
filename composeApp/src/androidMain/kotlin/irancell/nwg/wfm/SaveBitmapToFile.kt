package irancell.nwg.wfm

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID



actual fun SaveBitmapToFile(savePath : String,key : String,bitmap: Any):Any{

    lateinit var uri : Uri
    val context = (provideAppContext() as Context)
    val file = createImageFile(savePath,key)

    val out: OutputStream = FileOutputStream(file)
    (bitmap as Bitmap).compress(Bitmap.CompressFormat.JPEG, 100, out)
    out.flush()
    out.close()

   uri = InternalStorage.getUriForFile(context, file)
   return uri

}

private fun createImageFile(path : String,key : String): File {
    val uuid = UUID.randomUUID().toString()
    val imageFileName = "${uuid}${"@"}${key}.jpg"
    return File(path, imageFileName)
}

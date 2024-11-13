package irancell.nwg.wfm

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore

actual fun RotateImageUri(originalUri: String,key:String,angle: Float): String {

    val uri = Uri.parse(originalUri)
    println("checkAngle   ${(angle  )}")
    val rotatedUri = rotateImageAndSave(uri,key, angle)
    return rotatedUri.toString()
}

private fun rotateImageAndSave(uri: Uri, key: String, angle: Float): Uri {
    val context = provideAppContext() as Context
    var rotatedUri: Uri? = null

    val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

    try {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        println("checkAngle   ${(angle  )}")

        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        println("checkAngle   ${(matrix  )}")

        val savePath = InternalStorage.getProcessRouteEdited(context)

        rotatedUri = SaveBitmapToFile(savePath, key = key, rotatedBitmap) as Uri
        println("checkAngle   ${(rotatedUri  )}")
        rotatedBitmap.recycle()

    } finally {
        bitmap.recycle()
    }
    println("checkAngle   ${(uri  )}")
    println("checkAngle   ${(rotatedUri  )}")

    return rotatedUri ?: uri
}
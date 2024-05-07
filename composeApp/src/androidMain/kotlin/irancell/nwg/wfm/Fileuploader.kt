package irancell.nwg.wfm

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun FilePickerr(onFileSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->

            uris.forEach { uri ->
                onFileSelected(uri)
            }
        }
    )

}

fun saveFileToInternalStorage(context: Context, savePath: String): Boolean {
    return try {
        val uri = Uri.parse("file://$savePath")
        val inputStream = context.contentResolver.openInputStream(uri)
        val fileExtension = getExtensionFromUri(context, uri)
        val file = createInternalFile(context, fileExtension)
        val outputStream = FileOutputStream(file)
        inputStream?.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

private fun getExtensionFromUri(context: Context, uri: Uri): String {
    val mimeTypeMap = MimeTypeMap.getSingleton()
    return mimeTypeMap.getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: ""
}

private fun createInternalFile(context: Context, fileExtension: String): File {
    val internalStorageDir = context.filesDir
    val uuid = UUID.randomUUID().toString()
    return File.createTempFile("file_${uuid}", ".$fileExtension", internalStorageDir)
}
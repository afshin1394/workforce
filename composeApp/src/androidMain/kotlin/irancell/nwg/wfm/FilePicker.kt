package irancell.nwg.wfm

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

actual class FilePicker {



    actual companion object {
        lateinit var filePickerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

        @Composable
        actual fun onResult( key:String,onSuccess: (List<Pair<Any, Any>>) -> Unit) {
            val context = LocalContext.current
            filePickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        val data = result.data
                        val uris = mutableListOf<Uri>()
                        if (data != null) {
                            val clipData = data.clipData
                            if (clipData != null) {
                                for (i in 0 until clipData.itemCount) {
                                    uris.add(clipData.getItemAt(i).uri)
                                }
                            } else {
                                val uri = data.data
                                if (uri != null) {
                                    uris.add(uri)
                                }
                            }
                        }

                        val contentResolver = context.contentResolver
                        val files = mutableListOf<Pair<String, File>>()

                        val directory = File(  InternalStorage.getUploadFileRouteOriginal(provideAppContext()) , "UploadedFiles")
                        if (!directory.exists()) {
                            directory.mkdirs()
                        }
                        for (uri in uris) {
                            val fileName = getFileName(uri, contentResolver)
                            val fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))
                            //val destinationFile = File(directory, "$fileName")
                            val destinationFile = File(directory, "${"@"}${key}${"."}${fileName}")
                            saveFileToInternalStorage(uri, contentResolver, destinationFile)
                            files.add("${fileName}"!! to destinationFile)
                        }
                        onSuccess(files)
                    }
                }
            )
        }




        @Composable
        actual fun launchFilePicker() {

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*" // All files
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

            }
            filePickerLauncher.launch(intent)
        }



       actual fun openFile(filePath: Any, context: Any) {

           val file = File(filePath.toString())
            val uri = FileProvider.getUriForFile((context as Context), context.packageName + ".provider", file)
            val openFileIntent = Intent(Intent.ACTION_VIEW)
            openFileIntent.setDataAndType(uri, "*/*")
            openFileIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            openFileIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(openFileIntent)
        }

        private fun getFileName(uri: Uri, contentResolver: ContentResolver): String? {
            var name: String? = null
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (displayNameIndex != -1) {
                        name = it.getString(displayNameIndex)
                    }
                }
            }
            return name
        }

        private fun saveFileToInternalStorage(uri: Uri, contentResolver: ContentResolver, destinationFile: File) {
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(destinationFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}








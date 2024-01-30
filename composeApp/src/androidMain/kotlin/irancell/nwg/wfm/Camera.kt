package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.util.Objects
import java.util.UUID

actual class Camera {
    actual companion object {
        @SuppressLint("CoroutineCreationDuringComposition")
        @Composable
       actual  fun ImagePicker() {
            val context = LocalContext.current
            var currentPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }
            //var tempPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }
            val file = context.createImageFile(context)
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    if (success) currentPhotoUri = uri
                }
            )
            LaunchedEffect(Unit){
               cameraLauncher.launch(uri)
            }

        }


        fun Context.createImageFile(context : Context): File {
            // Create an image file name
            val uuid = UUID.randomUUID().toString()
            val imageFileName = "JPEG_" + uuid + "_"
            var folder : File? = null
            val path = context.filesDir.path + "/WFMImages/" + imageFileName
            folder = createFolder(path)

            return folder
        }

        fun createFolder(parent : String, s : String = "") : File{
            val folder = File(parent , s)
            if (!folder.exists())
                folder.mkdir()

            return folder
        }
    }
}
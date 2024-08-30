package irancell.nwg.wfm


import android.app.ActivityOptions
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID
actual class Camera {

    actual companion object {

        lateinit var cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>
        lateinit var uri: Uri
        var obj : Any? = null

        @Composable
        actual fun onResult(onSuccess: (uri: Any,obj : Any?) -> Unit) {
            cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    Log.i("cameraLauncher", "success: $success")
                    if (success) {

                        onSuccess(uri.toString(), obj)
                    }
                }
            )
        }

        @Composable
        actual fun launchCamera(obj: Any?,savePath: String, key: String) {
              this.obj = obj
              val context = LocalContext.current
              val file = createImageFile(savePath, key)
              Log.i("ImagePicker", "file.path: ${file.path}")

              uri = InternalStorage.getUriForFile(context, file)
              LaunchedEffect(cameraLauncher) {
                  try {
                      cameraLauncher.launch(uri)
                  }catch (_:Exception) {
                      Log.i("ImagePicker", "exception open camera:")
                  }
              }

        }


        private fun createImageFile(path: String, key: String): File {
            val uuid = UUID.randomUUID().toString()
            val imageFileName = "${uuid}${"@"}${key}.jpg"

            return File(path, imageFileName)
        }
    }
}






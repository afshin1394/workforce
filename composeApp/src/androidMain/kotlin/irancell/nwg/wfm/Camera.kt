package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects
import java.util.Random
import java.util.UUID

actual class Camera {

    actual companion object {

        lateinit var cameraLauncher : ManagedActivityResultLauncher<Uri,Boolean>
        lateinit var uri : Uri
        @Composable
        actual fun onResult( onSuccess: (uri: Any) -> Unit) {

            cameraLauncher =  rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    Log.i("cameraLauncher", "success: $success")
                    if (success) {

                        onSuccess(uri.toString())
                    }
                }
            )
        }
        @Composable
        actual fun launchCamera(savePath: String){
            val context = LocalContext.current
            val file = createImageFile(savePath)
            Log.i("ImagePicker", "file.path: ${file.path}")

            uri = InternalStorage.getUriForFile(context, file)
            cameraLauncher.launch(uri)
        }


        private fun createImageFile(path: String): File {
            val uuid = UUID.randomUUID().toString()
            val imageFileName = "${uuid}.jpg"

            return File(path, imageFileName)
        }


    }
}






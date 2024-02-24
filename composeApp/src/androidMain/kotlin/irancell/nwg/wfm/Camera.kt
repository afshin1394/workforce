package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
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
        @SuppressLint("CoroutineCreationDuringComposition")
        @Composable
        actual fun ImagePicker(savePath : String,onSuccess : (uri : Any) -> Unit) {
            val context = LocalContext.current
            val file = createImageFile(context.filesDir.path + savePath )
            Log.i("uriiiii", "ImagePicker: ${file.path}")

            val uri  = InternalStorage.getUriForFile(context,file)

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    Log.i("pathtt", "ImagePicker: ${file.path}")

                    if (success) {
                        onSuccess(uri)
                    }
                }
            )
            LaunchedEffect(Unit) {
                cameraLauncher.launch(uri)
                Log.i("uriiiii", "ImagePicker: $uri")
            }

        }


        private fun createImageFile(path : String): File {
            val uuid = UUID.randomUUID().toString()
            val imageFileName = "${uuid}.jpg"

            return File(path, imageFileName)
        }




    }
}
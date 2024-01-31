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
        actual fun ImagePicker() {
            val context = LocalContext.current
            var currentPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }
            val file = context.createImageFile()
            val uri = FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )

            val cameraLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.TakePicture(),
                onResult = { success ->
                    if (success) {
                        currentPhotoUri = uri
                    }
                }
            )
            LaunchedEffect(Unit) {
                cameraLauncher.launch(uri)
                Log.i("uriiiii", "ImagePicker: $uri")
            }

        }


        private fun Context.createImageFile(): File {
            val uuid = UUID.randomUUID().toString()
            val pathToDirectory = InternalStorage.createFolderFromPath( this,"/wfmImages/suspend/1234")
            val imageFileName = "${uuid}.jpg"

            return File(pathToDirectory, imageFileName)
        }




    }
}
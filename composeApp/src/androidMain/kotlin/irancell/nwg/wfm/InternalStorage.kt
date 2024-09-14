package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import domain.models.PhotoDomain
import domain.models.form_struct.ValueDomain
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects

actual class InternalStorage {
    actual companion object {
        fun getUriForFile(context: Context, file: File): Uri {
            return FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )
        }


        fun createFolder(parent: String, name: String): File {
            val folder = File(parent, name)
            if (!folder.exists())
                folder.mkdir()

            return folder
        }

        fun createFolderFromPath(context: Context, path: String, name: String = ""): File {

            var folder: File? = null
            val path = context.filesDir.path + path
            folder = createFolder(path, name)
            Log.i("folderexist", folder.exists().toString())
            return folder
        }

        actual fun initWFMImages(context: Any) {
            val ctx = context as Context
            createFolderFromPath(ctx, "/wfmImages/")
        }

        actual fun initSuspendImages(context: Any) {
            val ctx = context as Context
            createFolderFromPath(ctx, "/wfmImages/", "")

        }

        actual fun initProcessImages(context: Any) {
            val ctx = context as Context
            createFolderFromPath(ctx, "/wfmImages/", "")
        }

        actual fun createWorkItemImages(context: Any, pathName: String, name: String): Any {
            val ctx = context as Context
            return createFolderFromPath(ctx, "/wfmImages/$pathName/", name)
        }

        actual fun getWFMRoute(context: Any): String {
            val ctx = context as Context
            return ctx.filesDir.path + "/wfmImages/"
        }

        actual fun getSuspendRouteEdited(context: Any): String {
            val ctx = context as Context
            return ctx.filesDir.path + "/wfmImages/"
        }

        actual fun getProcessRouteEdited(context: Any): String {
            val ctx = context as Context
            return ctx.filesDir.path + "/wfmImages/"
        }

        actual fun getSuspendRouteOriginal(context: Any): String {
            val ctx = context as Context
            return ctx.filesDir.path + "/wfmImages/"
        }


        actual fun getUploadFileRouteOriginal(context: Any): String {
            val ctx = context as Context
            return ctx.filesDir.path + "/wfmFiles/"
        }

        actual fun getProcessRouteOriginal(context: Any): String {
            val ctx = context as Context
            return ctx.filesDir.path + "/wfmImages/"
        }

        actual fun removeFiles(paths: List<Any>?) {
            paths?.forEach { item ->
                when (item) {
                    is PhotoDomain -> {
                        deleteUri(item.origin_uri)
                        deleteUri(item.edited_uri)
                    }

                    is ValueDomain -> {
                        deleteUri(item.value)
                    }

                    is String -> {
                        deleteUri(item)
                    }

                    else -> {
                        Log.w("FileDeletion", "Unsupported type: ${item::class}")
                    }
                }
            }
        }

        private fun deleteUri(uri: String?) {
            if (uri.isNullOrEmpty()) return

            val isContentUri = uri.startsWith("content://")
            try {
                if (isContentUri) {
                    deleteContentUri(uri)
                } else {
                    deleteFileUri(uri)
                }
            } catch (e: Exception) {
                Log.e("FileDeletion", "Error deleting file: $uri", e)
            }
        }

        private fun deleteContentUri(uri: String) {
            val contentUri = Uri.parse(uri)
            val contentResolver = (provideAppContext() as Context).contentResolver
            val deletedRows = contentResolver.delete(contentUri, null, null)
            if (deletedRows > 0) {
                Log.i("FileDeletion", "Deleted content URI: $uri")
            } else {
                Log.w("FileDeletion", "Failed to delete content URI: $uri")
            }
        }

        private fun deleteFileUri(uri: String) {
            val file = File(uri)
            if (file.exists() && file.delete()) {
                Log.i("FileDeletion", "Deleted file: $uri")
            } else {
                Log.w("FileDeletion", "Failed to delete file: $uri")
            }
        }


    }
}
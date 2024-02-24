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
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects

actual class InternalStorage {
    actual companion object {
        fun getUriForFile(context : Context,file : File) : Uri {
            return FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )
        }


          fun createFolder(parent: String,name : String): File {
            val folder = File(parent,name)
            if (!folder.exists())
                folder.mkdir()

            return folder
        }

         fun createFolderFromPath(context: Context, path: String,name : String = ""): File {

            var folder: File? = null
            val path = context.filesDir.path + path
            folder = createFolder(path,name)

            Log.i("folderexist",folder.exists().toString())
            return folder
        }

        actual fun initWFMImages(context: Any) : Any {
            val ctx =context as Context
           return createFolderFromPath(ctx,"/wfmImages/")
        }
        actual fun initSuspendImages(context: Any) : Any {
            val ctx = context as Context
           return createFolderFromPath(ctx,"/wfmImages/","Suspend")
        }
        actual fun initProcessImages(context: Any) : Any {
            val ctx =context as Context
           return createFolderFromPath(ctx,"/wfmImages/","Process")
        }

        actual fun createWorkItemImages(context: Any,pathName : String, name: String): Any {
            val ctx =context as Context
            return createFolderFromPath(ctx,"/wfmImages/$pathName/",name)
        }

    }
}
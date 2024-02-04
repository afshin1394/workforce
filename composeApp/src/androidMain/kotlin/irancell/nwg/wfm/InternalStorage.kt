package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects

actual class InternalStorage {
    companion object {
        fun getUriForFile(context : Context,file : File) : Uri {
            return FileProvider.getUriForFile(
                Objects.requireNonNull(context),
                context.packageName + ".provider", file
            )
        }


        fun createFolder(parent: String, s: String = ""): File {
            val folder = File(parent, s)
            if (!folder.exists())
                folder.mkdir()

            return folder
        }

        fun createFolderFromPath(context: Context, path: String): File {
            var i = 0
            var folder: File? = null
            val path = context.filesDir.path + path
            folder = createFolder(path)


            return folder
        }
    }
}
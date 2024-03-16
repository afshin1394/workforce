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

        actual fun initWFMImages(context: Any)  {
            val ctx =context as Context
            createFolderFromPath(ctx,"/wfmImages/")
        }
        actual fun initSuspendImages(context: Any)  {
            val ctx = context as Context
            createFolderFromPath(ctx,"/wfmImages/","Suspend")
            createFolderFromPath(ctx,"/wfmImages/Suspend","Edited")
            createFolderFromPath(ctx,"/wfmImages/Suspend","Original")

        }
        actual fun initProcessImages(context: Any)  {
            val ctx =context as Context
            createFolderFromPath(ctx,"/wfmImages/","Process")
            createFolderFromPath(ctx,"/wfmImages/Process/","Edited")
            createFolderFromPath(ctx,"/wfmImages/Process/","Original")

        }

        actual fun createWorkItemImages(context: Any,pathName : String, name: String): Any {
            val ctx =context as Context
            return createFolderFromPath(ctx,"/wfmImages/$pathName/",name)
        }

        actual fun getWFMRoute(context: Any): String {
            val ctx =context as Context
            return ctx.filesDir.path + "/wfmImages/"
        }

        actual fun getSuspendRouteEdited(context: Any): String {
            val ctx =context as Context
            return ctx.filesDir.path + "/wfmImages/Suspend/Edited/"
        }

        actual fun getProcessRouteEdited(context: Any): String {
            val ctx =context as Context
            return ctx.filesDir.path + "/wfmImages/Process/Edited/"
        }

        actual fun getSuspendRouteOriginal(context: Any): String {
            val ctx =context as Context
            return ctx.filesDir.path + "/wfmImages/Suspend/Original/"
        }

        actual fun getProcessRouteOriginal(context: Any): String {
            val ctx =context as Context
            return ctx.filesDir.path + "/wfmImages/Process/Original/"
        }

    }
}
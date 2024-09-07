package irancell.nwg.wfm

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import android.content.Intent
@SuppressLint("ObsoleteSdkInt")
actual fun InstallApk(filePath: String) {

    val apkFile = File(filePath)

    val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile( (provideAppContext() as Context), "${ (provideAppContext() as Context).packageName}.provider", apkFile)
    } else {
        Uri.fromFile(apkFile)
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(uri, "application/vnd.android.package-archive")
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    (provideAppContext() as Context).startActivity(intent)
}
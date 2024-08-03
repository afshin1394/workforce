package irancell.nwg.wfm

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream


actual fun UriToFile(uri: String): FileData? {
    val context = provideAppContext()
    try {
        val uriParsed = Uri.parse(uri)
        val fileName = uriParsed.lastPathSegment ?: return null
        val destinationFile = File((context as Context).cacheDir, fileName)
        (context as Context).contentResolver.openInputStream(uriParsed)?.use { inputStream ->
            FileOutputStream(destinationFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        return FileData(fileName, destinationFile.absolutePath, destinationFile.length())
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


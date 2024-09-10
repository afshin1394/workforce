package irancell.nwg.wfm

import android.util.Log
import java.io.File
import java.util.zip.ZipInputStream

actual fun Unzip(zipFilePath: String, targetDirectoryPath: String): String? {
    val zipFile = File(zipFilePath)
    val targetDirectory = File(targetDirectoryPath)

    var apkFilePath: String? = null

    ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
        var entry = zipInputStream.nextEntry
        Log.d("entries","entry" + entry.name)
        while (entry != null && entry.name != "__MACOSX/._WFM.apk") {
            val file = File(targetDirectory, entry.name)
            if (entry.isDirectory) {
                file.mkdirs()
            } else {
                file.outputStream().use { outputStream ->
                    zipInputStream.copyTo(outputStream)
                }

                if (entry.name.equals("WFM.apk")) {
                    apkFilePath = file.absolutePath
                }
            }
            entry = zipInputStream.nextEntry
        }
        zipInputStream.closeEntry()
    }

    return apkFilePath
}


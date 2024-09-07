package irancell.nwg.wfm

import java.io.File
import java.util.zip.ZipInputStream

actual fun Unzip(zipFilePath: String, targetDirectoryPath: String): String? {
    val zipFile = File(zipFilePath)
    val targetDirectory = File(targetDirectoryPath)

    var apkFilePath: String? = null

    ZipInputStream(zipFile.inputStream()).use { zipInputStream ->
        var entry = zipInputStream.nextEntry
        while (entry != null) {
            val file = File(targetDirectory, entry.name)
            if (entry.isDirectory) {
                file.mkdirs()
            } else {
                file.outputStream().use { outputStream ->
                    zipInputStream.copyTo(outputStream)
                }

                if (entry.name.endsWith(".apk")) {
                    apkFilePath = file.absolutePath
                }
            }
            entry = zipInputStream.nextEntry
        }
        zipInputStream.closeEntry()
    }

    return apkFilePath
}


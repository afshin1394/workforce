package irancell.nwg.wfm

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

actual fun ZipFiles(fileDataList: List<String>, zipFilePath: String): FileData? {
    return try {
        val zipFile = File(zipFilePath)
        val zipOut = ZipOutputStream(FileOutputStream(zipFile))
        zipOut.setLevel(2)

        fileDataList.forEach { fileData ->
            val fileToZip = File(fileData)
            if (fileToZip.exists()) {
                FileInputStream(fileToZip).use { fis ->
                    val zipEntry = ZipEntry(fileToZip.name)
                    zipOut.putNextEntry(zipEntry)
                    fis.copyTo(zipOut)
                    zipOut.closeEntry()
                }
            } else {
                println("File not found: ${fileData}")
            }
        }

        zipOut.close()
        FileData(zipFile.name, zipFile.absolutePath, zipFile.length())
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

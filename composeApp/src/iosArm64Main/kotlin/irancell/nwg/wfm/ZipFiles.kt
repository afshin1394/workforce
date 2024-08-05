package irancell.nwg.wfm

import kotlinx.cinterop.*
import platform.Foundation.*
import platform.posix.*

actual fun ZipFiles(fileDataList: List<String>, zipFilePath: String): FileData? {
    return try {
        val tempDir = NSTemporaryDirectory()
        val zipFilePath = tempDir + zipFileName
        val paths = fileDataList.map { it.path }

        if (SSZipArchive.createZipFileAtPath(zipFilePath, paths)) {
            FileData(zipFileName, zipFilePath, NSFileManager.defaultManager().attributesOfItemAtPath(zipFilePath, null)?.fileSize() ?: 0)
        } else {
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
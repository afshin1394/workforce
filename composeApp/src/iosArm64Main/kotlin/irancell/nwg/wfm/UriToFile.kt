package irancell.nwg.wfm

import platform.Foundation.*
import platform.posix.*
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned

actual fun UriToFile(uri: String): String? {
    return try {
        val url = NSURL.URLWithString(uri) ?: return null
        val data = NSData.dataWithContentsOfURL(url) ?: return null

        val fileName = url.lastPathComponent ?: return null
        val tempDir = NSTemporaryDirectory()
        val destinationPath = tempDir + fileName
        val destinationFile = File(destinationPath)

        val outputStream = fopen(destinationPath, "wb") ?: return null
        data.bytes?.let { bytes ->
            data.usePinned { pinned ->
                fwrite(pinned.addressOf(0), 1, data.length.toULong(), outputStream)
            }
        }
        fclose(outputStream)
        return FileData(fileName, destinationPath, data.length.toLong())
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
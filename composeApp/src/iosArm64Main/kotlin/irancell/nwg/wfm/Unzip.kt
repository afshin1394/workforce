package irancell.nwg.wfm
import platform.Foundation.*
import platform.posix.remove
actual fun Unzip(zipFilePath: String, targetDirectoryPath: String):String? {
    val fileManager = NSFileManager.defaultManager()
    val targetDirectoryUrl = NSURL.fileURLWithPath(targetDirectoryPath)
    val zipFileUrl = NSURL.fileURLWithPath(zipFilePath)

    val success = fileManager.unzipItemAtURL(zipFileUrl, toURL = targetDirectoryUrl, error = null)
    if (!success) {
        throw Exception("Unzipping failed")
    }
    return ""
}
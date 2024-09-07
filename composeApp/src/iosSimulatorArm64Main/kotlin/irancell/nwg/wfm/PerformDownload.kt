package irancell.nwg.wfm

import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask
import platform.Foundation.stringByAppendingPathComponent
import kotlin.native.concurrent.freeze

actual suspend fun PerformDownload(url: String): Result<DownloadResult> ?{
    return try {
        val paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
        val documentsDirectory = paths.first() as String
        val outputFile = documentsDirectory.stringByAppendingPathComponent("file.zip")
        val destination = FileDestination(outputFile)

        // استفاده از متد برای دانلود فایل
        DownloadFile(url, destination)

        if (NSFileManager.defaultManager.fileExistsAtPath(outputFile)) {
            println("File downloaded successfully at: $outputFile")
            Result.success(DownloadResult(outputFile, documentsDirectory))
        } else {
            println("File download failed.")
            Result.failure(Exception("File download failed"))
        }
    } catch (e: Exception) {
        println("Error during download: ${e.message}")
        Result.failure(e)
    }
}
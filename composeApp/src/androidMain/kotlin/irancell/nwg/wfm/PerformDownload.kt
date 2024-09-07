package irancell.nwg.wfm
import android.content.Context
import android.util.Log
import java.io.File


actual suspend fun PerformDownload(url: String): Result<DownloadResult>? {
    val outputDir = (provideAppContext() as Context).filesDir
    val outputFile = File(outputDir, "file.zip")
    val destination = FileDestination(outputFile)

    return try {
        DownloadFile(url, destination)
        if (outputFile.exists()) {
            Log.d("Download", "File downloaded successfully at: ${outputFile.absolutePath}")
            Result.success(DownloadResult(outputFile.absolutePath, outputDir.absolutePath))
        } else {
            throw Exception("File download failed. File does not exist.")
        }
    } catch (e: Exception) {
        Log.e("Download", "File download failed: ${e.message}")
        Result.failure(e)
    }
}
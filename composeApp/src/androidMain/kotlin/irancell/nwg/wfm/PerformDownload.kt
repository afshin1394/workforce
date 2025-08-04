package irancell.nwg.wfm
import android.content.Context
import android.util.Log
import java.io.File


actual suspend fun PerformDownload(url: String,fileName : String): Result<DownloadResult>? {
    val outputDir = (provideAppContext() as Context).filesDir
    val outputFile = File(outputDir, fileName)
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

actual suspend fun PerformGZIPDownload(
    url: String,
    fileName: String,
    onProgress: (DownloadState) -> Unit
) {
    val outputDir = (provideAppContext() as Context).filesDir
    val outputFile = File(outputDir, fileName)
    val destination = FileDestination(outputFile)

    try {
        DownloadGZIP(url, destination) { downloadState ->
            when (downloadState) {
                is DownloadState.Started -> {
                    // Forward start state
                    onProgress(downloadState)
                }
                is DownloadState.Progress -> {
                    val percent = downloadState.totalBytes?.let {
                        (downloadState.bytesDownloaded * 100 / it).toInt()
                    } ?: -1
                    println("Download progress: $percent% (${downloadState.bytesDownloaded} bytes)")
                    // Forward progress state
                    onProgress(downloadState)
                }
                is DownloadState.Finished -> {
                    println("Download finished!")
                    onProgress(downloadState)
                }
                is DownloadState.Failed -> {
                    println("Download failed: ${downloadState.exception.message}")
                    onProgress(downloadState)
                }
            }
        }
    } catch (e: Exception) {
        // If DownloadGZIP throws, forward failure state
        onProgress(DownloadState.Failed(e))
    }
}

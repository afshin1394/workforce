package irancell.nwg.wfm
sealed class DownloadState {
    object Started : DownloadState()
    data class Progress(val bytesDownloaded: Long, val totalBytes: Long?) : DownloadState()
    object Finished : DownloadState()
    data class Failed(val exception: Throwable) : DownloadState()
}
data class DownloadResult(val zipFilePath: String, val targetDirectoryPath: String)

expect suspend fun PerformDownload(url: String,fileName : String): Result<DownloadResult>?
expect suspend fun PerformGZIPDownload(
    url: String,
    fileName: String,
    onProgress: (DownloadState) -> Unit
)
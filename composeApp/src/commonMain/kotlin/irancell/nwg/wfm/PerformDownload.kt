package irancell.nwg.wfm

data class DownloadResult(val zipFilePath: String, val targetDirectoryPath: String)

expect suspend fun PerformDownload(url: String): Result<DownloadResult>?
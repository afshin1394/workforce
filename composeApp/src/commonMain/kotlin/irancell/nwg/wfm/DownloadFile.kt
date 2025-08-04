package irancell.nwg.wfm

expect suspend fun DownloadFile(url: String, output: FileDestination)
expect suspend fun DownloadGZIP(url: String, output: FileDestination,onProgress: (DownloadState) -> Unit)

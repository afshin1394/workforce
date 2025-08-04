package irancell.nwg.wfm

import platform.Foundation.*

actual suspend fun DownloadFile(url: String, output: FileDestination) {
    suspendCancellableCoroutine<Unit> { continuation ->
        val nsUrl = NSURL.URLWithString(url)
        val request = NSURLRequest.requestWithURL(nsUrl!!)
        val session = NSURLSession.sharedSession

        val task = session.dataTaskWithRequest(request) { data, _, error ->
            when {
                error != null -> continuation.resumeWithException(Exception(error.localizedDescription))
                data != null -> {
                    output.write(data.toByteArray())
                    continuation.resume(Unit)
                }
                else -> continuation.resumeWithException(Exception("Unknown error"))
            }
        }
        task.resume()
        continuation.invokeOnCancellation { task.cancel() }
    }
}

actual suspend fun DownloadGZIP(
    url: String,
    output: FileDestination,
    onProgress: (DownloadState) -> Unit
) {}
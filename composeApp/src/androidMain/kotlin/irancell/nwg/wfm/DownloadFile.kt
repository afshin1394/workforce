package irancell.nwg.wfm
import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import utils.Token
import java.io.*
import java.util.zip.GZIPInputStream

actual suspend fun DownloadFile(url: String, output: FileDestination) {

    val httpClient = HttpClient()
    httpClient.use { client ->
        val response: ByteArray = client.get(url).body()
        Log.d("DownloadFile", "DownloadFile: "+response.toString())
        output.write(response)
    }
}



actual suspend fun DownloadGZIP(
    url: String,
    output: FileDestination,
    onProgress: (DownloadState) -> Unit
) {
    val httpClient = HttpClient {
        defaultRequest {
            headers {
                append("Authorization", "Token ${getSharedPref().getString(Token)}")
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1_200_000
            connectTimeoutMillis = 5_000
            socketTimeoutMillis = 1_200_000
        }
    }

    httpClient.use { client ->
        val response: HttpResponse = client.get(url)
        val contentLength = response.contentLength() ?: -1L
        println("Download started: ${contentLength} bytes expected")

        withContext(Dispatchers.IO) {
            val gzipInputStream = GZIPInputStream(response.bodyAsChannel().toInputStream())
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            var totalRead = 0L
            var lastLogTime = System.currentTimeMillis()

            output.writeStreamed { writeChunk ->
                gzipInputStream.use { input ->
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        val chunk = buffer.copyOfRange(0, bytesRead)
                        writeChunk(chunk)
                        totalRead += bytesRead

                        val now = System.currentTimeMillis()
                        if (now - lastLogTime > 1000) {
                            if (contentLength > 0) {
                                val percent = (totalRead * 100) / contentLength
                                println("Downloaded: $totalRead bytes ($percent%)")
                            } else {
                                println("Downloaded: $totalRead bytes")
                            }
                            lastLogTime = now
                        }
                    }
                }
            }

            println("Download complete. Total bytes: $totalRead")
        }
    }
}

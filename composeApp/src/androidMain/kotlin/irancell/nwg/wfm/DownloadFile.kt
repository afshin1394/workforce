package irancell.nwg.wfm
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

actual suspend fun DownloadFile(url: String, output: FileDestination) {
    val httpClient = HttpClient()
    httpClient.use { client ->
        val response: ByteArray = client.get(url).body()
        output.write(response)
    }
}
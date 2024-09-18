package data


import data.network.response.ipDetection.IpDetectionResponse
import domain.repository.IIpDetectionRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class IpDetectionRepositoryImpl(
    private val httpClient: HttpClient,
) : IIpDetectionRepository {
    override suspend fun detectIp(): IpDetectionResponse {
        val response = httpClient.get("https://api.country.is")
            .body<IpDetectionResponse>()
        Napier.log(
            LogLevel.ASSERT,
            "ipDetection",
            message = "ip ${response.ip} country: ${response.country}"
        )
        return response
    }
}
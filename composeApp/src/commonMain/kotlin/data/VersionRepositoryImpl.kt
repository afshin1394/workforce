package data

import data.network.request.version.VersionRequest
import data.network.response.profile.ProfileNetworkResponse
import data.network.response.version.GetVersionNetworkResponse
import data.network.response.version.SendVersionNetworkResponse
import domain.repository.IVersionRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class VersionRepositoryImpl(
    private val httpClient: HttpClient,
    private val httpClientWithToken: HttpClient,
) : IVersionRepository {
    override suspend fun fetchSendVersion(versionRequest: VersionRequest): SendVersionNetworkResponse {
        val response = httpClientWithToken.post("mobile/device-info/"){
            setBody(versionRequest)
        }.body<SendVersionNetworkResponse>()

        return response
    }

    override suspend fun fetchGetVersion(): GetVersionNetworkResponse {
        return httpClientWithToken.get("mobile/version-check/").body<GetVersionNetworkResponse>()
    }
}
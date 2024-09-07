package domain.repository


import data.network.request.version.VersionRequest
import data.network.response.version.GetVersionNetworkResponse
import data.network.response.version.SendVersionNetworkResponse

interface IVersionRepository {

    suspend fun fetchSendVersion(versionRequest: VersionRequest) : SendVersionNetworkResponse
    suspend fun fetchGetVersion() : GetVersionNetworkResponse

}
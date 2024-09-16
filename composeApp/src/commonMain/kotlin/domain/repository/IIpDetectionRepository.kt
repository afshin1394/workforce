package domain.repository

import data.network.request.auth.LoginNetworkRequest
import data.network.request.auth.ResendNetworkRequest
import data.network.request.auth.VerifyNetworkRequest
import data.network.response.auth.LoginNetworkResponse
import data.network.response.auth.VerifyNetworkResponse
import data.network.response.ipDetection.IpDetectionResponse
import io.ktor.http.HttpStatusCode

interface IIpDetectionRepository {

    suspend fun detectIp(): IpDetectionResponse

}
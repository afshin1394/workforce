package data

import data.network.response.cr.CRNetworkResponse
import domain.repository.ICRRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.request

class CRRepositoryImpl(
    private val httpClient: HttpClient,
    ) : ICRRepository {
    override suspend fun fetchAllCR(queryParam: String) : List<CRNetworkResponse> {

     return   httpClient.get("workforce_management/user/cr/"){
            parameter("q",queryParam)
        }.body<List<CRNetworkResponse>>()
    }
}
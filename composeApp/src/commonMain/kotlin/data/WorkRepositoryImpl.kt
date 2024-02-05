package data

import data.network.response.WorksNetworkResponse
import domain.repository.IWorkRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers

class WorkRepositoryImpl(
    private val httpClient: HttpClient
) : IWorkRepository {
    override suspend fun fetchWorks(): List<WorksNetworkResponse> {
         return  httpClient.get("workforce_management/user/my-tasks/").body<List<WorksNetworkResponse>>()
    }
}
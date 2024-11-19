package data

import data.network.request.ChangeAvailabilityRequest
import domain.repository.IAvailabilityRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import data.network.response.ObjectID
import data.network.response.availability.AvailabilityNetworkResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AvailabilityRepositoryImpl(
    private val httpClient: HttpClient
) : IAvailabilityRepository {
    override suspend fun fetchAvailability(): AvailabilityNetworkResponse {
        GlobalScope.launch(Dispatchers.Main) {
            println("fetchAvailability AvailabilityRepositoryImpl")
        }
        return httpClient.get("workforce_management/user/is_online/").body<AvailabilityNetworkResponse>()
    }

    override suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest): Pair<HttpStatusCode,String> {
        val apiCall = httpClient.post("workforce_management/user/ready-to-work/") {
            setBody(
                body = changeAvailabilityRequest,
            )
        }
        val body = apiCall.bodyAsText()
        val status = apiCall.status

        val objectIdJson  = body.substringAfter("=").substringBefore(" ")
        val objectID = Json.decodeFromString<ObjectID>(objectIdJson).detail
        Napier.log(LogLevel.ASSERT,"objectId", message = objectID)
        return Pair(status,objectID)

    }


}
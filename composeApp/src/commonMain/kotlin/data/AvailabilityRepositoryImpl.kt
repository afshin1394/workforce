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

class AvailabilityRepositoryImpl(
    private val httpClient: HttpClient
) : IAvailabilityRepository {
    override suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest): String {
        val response = httpClient.post("workforce_management/user/ready-to-work/") {
            setBody(
                body = changeAvailabilityRequest,
            )
        }.bodyAsText()

        val objectIdJson  = response.substringAfter("=").substringBefore(" ")
        val objectID = Json.decodeFromString<ObjectID>(objectIdJson).detail
        Napier.log(LogLevel.ASSERT,"objectId", message = objectID)
        return objectID

    }
}
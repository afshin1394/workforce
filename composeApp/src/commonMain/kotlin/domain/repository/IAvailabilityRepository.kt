package domain.repository

import data.network.request.ChangeAvailabilityRequest
import data.network.response.availability.AvailabilityNetworkResponse
import io.ktor.http.HttpStatusCode

interface IAvailabilityRepository {
    suspend fun fetchAvailability() : AvailabilityNetworkResponse
    suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest) : Pair<HttpStatusCode,String>
}
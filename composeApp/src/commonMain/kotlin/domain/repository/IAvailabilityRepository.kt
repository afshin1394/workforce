package domain.repository

import data.network.request.ChangeAvailabilityRequest
import io.ktor.http.HttpStatusCode

interface IAvailabilityRepository {
    suspend fun fetchAvailability() : Boolean
    suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest) : Pair<HttpStatusCode,String>
}
package domain.repository

import data.network.request.ChangeAvailabilityRequest

interface IAvailabilityRepository {
    suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest) : String
}
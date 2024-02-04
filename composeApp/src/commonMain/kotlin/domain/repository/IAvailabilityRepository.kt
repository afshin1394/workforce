package domain.repository

import data.network.ChangeAvailabilityRequest

interface IAvailabilityRepository {
    suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest)
}
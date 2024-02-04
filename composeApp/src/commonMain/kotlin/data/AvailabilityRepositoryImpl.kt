package data

import data.network.ChangeAvailabilityRequest
import data.network.SendLocationRequest
import domain.repository.IAvailabilityRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import irancell.nwg.wfm.getSharedPref
import utils.Availability

class AvailabilityRepositoryImpl(
    private val httpClient: HttpClient
) : IAvailabilityRepository {
    override suspend fun changeAvailability(changeAvailabilityRequest: ChangeAvailabilityRequest) {
        httpClient.post("workforce_management/ready-to-work/") {
            setBody(
              body =  changeAvailabilityRequest,
            )
        }
    }
}
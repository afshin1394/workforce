package data

import data.network.request.live_location.LiveLocationRequest
import database.AppDatabase
import database.entity.GeneralLocationEntity
import domain.repository.IGeneralLocationRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody


class GeneralLocationRepositoryImpl(
    private val db: AppDatabase,
    private val httpClient: HttpClient,

) :
    IGeneralLocationRepository {
    override suspend fun insert(generalLocation: GeneralLocationEntity) {

        db.generalLocationDao().insert(generalLocation)

    }

    override suspend fun selectAll(): List<GeneralLocationEntity> {

        return db.generalLocationDao().selectAll()

    }

    override suspend fun selectUnSend(): List<GeneralLocationEntity> {


        return db.generalLocationDao().selectAllNotSentLocation()

    }

    override suspend fun updateUnSend() {


        db.generalLocationDao().updateAllAsSent()
    }

    override suspend fun deleteSent() {

        db.generalLocationDao().deleteAllSent()

    }

    override suspend fun sendLocationToServer(liveLocationRequest: List<LiveLocationRequest>) {


        httpClient.post("workforce_management/user/live-location/") {
            setBody(
               liveLocationRequest
            )
        }
    }
}
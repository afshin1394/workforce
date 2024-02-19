package data

import data.network.request.live_location.LiveLocationRequest
import domain.repository.IGeneralLocationRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import irancell.nwg.wfm.db.GeneralLocationEntity
import irancell.nwg.wfm.db.WFMDatabase

class GeneralLocationRepositoryImpl(
    private val wfmDatabase: WFMDatabase,
    private val httpClient: HttpClient
) :
    IGeneralLocationRepository {
    override suspend fun insert(generalLocation: GeneralLocationEntity) {
        wfmDatabase.generalLocationEntityQueries.insert(
            generalLocation.latitude,
            generalLocation.longitude,
            generalLocation.datetime,
            generalLocation.isSent
        )
    }

    override suspend fun selectAll(): List<GeneralLocationEntity> {
        return wfmDatabase.generalLocationEntityQueries.selectAll().executeAsList()
    }

    override suspend fun selectUnSend(): List<GeneralLocationEntity> {
        return wfmDatabase.generalLocationEntityQueries.selectAllNotSentLocation().executeAsList()
    }

    override suspend fun updateUnSend() {
        wfmDatabase.generalLocationEntityQueries.update()
    }

    override suspend fun deleteSent() {
        wfmDatabase.generalLocationEntityQueries.deleteAllSent()
    }

    override suspend fun sendLocationToServer(liveLocationRequest: List<LiveLocationRequest>) {


        httpClient.post("workforce_management/user/live-location/") {
            setBody(
               liveLocationRequest
            )
        }
    }
}
package data

import data.network.SendLocationRequest
import domain.repostory.IGeneralLocationRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import irancell.nwg.wfm.db.GeneralLocation
import irancell.nwg.wfm.db.WFMDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GeneralLocationRepositoryImpl(
    private val wfmDatabase: WFMDatabase,
    private val httpClient: HttpClient
) :
    IGeneralLocationRepository {
    override suspend fun insert(generalLocation: GeneralLocation) {
        wfmDatabase.generalLocationQueries.insert(
            generalLocation.latitude,
            generalLocation.longitude,
            generalLocation.datetime,
            generalLocation.isSent
        )
    }

    override suspend fun selectAll(): List<GeneralLocation> {
        return wfmDatabase.generalLocationQueries.selectAll().executeAsList()
    }

    override suspend fun selectUnSend(): List<GeneralLocation> {
        return wfmDatabase.generalLocationQueries.selectAllNotSentLocation().executeAsList()
    }

    override suspend fun updateUnSend() {
        wfmDatabase.generalLocationQueries.update()
    }

    override suspend fun deleteSent() {
        wfmDatabase.generalLocationQueries.deleteAllSent()
    }

    override suspend fun sendLocationToServer(generalLocation: GeneralLocation) {
        GlobalScope.launch(Dispatchers.Main) {
            Napier.log(
                LogLevel.ASSERT,
                tag = "serviice",
                message = "sendLocationToServer"
            )
        }

        httpClient.post("workforce_management/") {
            setBody(
                SendLocationRequest(
                    generalLocation.latitude.toDouble(),
                    generalLocation.longitude.toDouble()
                )
            )
        }
    }
}
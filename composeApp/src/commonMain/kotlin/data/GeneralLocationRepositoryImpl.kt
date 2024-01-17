package data

import domain.IGeneralLocationRepository
import irancell.nwg.wfm.db.GeneralLocation
import irancell.nwg.wfm.db.WFMDatabase

class GeneralLocationRepositoryImpl(private val wfmDatabase: WFMDatabase) : IGeneralLocationRepository {
    override fun insert(generalLocation: GeneralLocation) {
        wfmDatabase.generalLocationQueries.insert(generalLocation.latitude,generalLocation.longitude,generalLocation.datetime,generalLocation.isSent)
    }

    override fun selectAll(): List<GeneralLocation> {
       return wfmDatabase.generalLocationQueries.selectAll().executeAsList()
    }

    override fun selectUnSend(): List<GeneralLocation> {
        return wfmDatabase.generalLocationQueries.selectAllNotSentLocation().executeAsList()
    }

    override fun updateUnSend() {
        wfmDatabase.generalLocationQueries.update()
    }

    override fun deleteSent() {
        wfmDatabase.generalLocationQueries.deleteAllSent()
    }
}
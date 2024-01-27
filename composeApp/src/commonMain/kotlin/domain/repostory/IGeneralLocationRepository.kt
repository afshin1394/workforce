package domain.repostory

import app.cash.sqldelight.db.SqlDriver
import irancell.nwg.wfm.db.GeneralLocation

interface IGeneralLocationRepository {
   suspend fun insert(generalLocation: GeneralLocation)
   suspend fun selectAll(): List<GeneralLocation>
   suspend fun selectUnSend(): List<GeneralLocation>
   suspend fun updateUnSend()
   suspend fun deleteSent()

   suspend fun sendLocationToServer(generalLocation: GeneralLocation)
}
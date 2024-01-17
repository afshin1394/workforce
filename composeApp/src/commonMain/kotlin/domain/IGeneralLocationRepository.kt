package domain

import app.cash.sqldelight.db.SqlDriver
import irancell.nwg.wfm.db.GeneralLocation

interface IGeneralLocationRepository {
   fun insert(generalLocation: GeneralLocation)
   fun  selectAll() : List<GeneralLocation>
   fun  selectUnSend() : List<GeneralLocation>
   fun  updateUnSend()
   fun deleteSent()
}
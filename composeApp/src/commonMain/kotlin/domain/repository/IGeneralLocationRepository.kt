package domain.repository

import irancell.nwg.wfm.db.GeneralLocationEntity

interface IGeneralLocationRepository {
   suspend fun insert(generalLocation: GeneralLocationEntity)
   suspend fun selectAll(): List<GeneralLocationEntity>
   suspend fun selectUnSend(): List<GeneralLocationEntity>
   suspend fun updateUnSend()
   suspend fun deleteSent()

   suspend fun sendLocationToServer(generalLocation: GeneralLocationEntity)
}
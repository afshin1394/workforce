package domain.repository

import data.network.request.live_location.LiveLocationRequest
import database.entity.GeneralLocationEntity


interface IGeneralLocationRepository {
   suspend fun insert(generalLocation: GeneralLocationEntity)
   suspend fun selectAll(): List<GeneralLocationEntity>
   suspend fun selectUnSend(): List<GeneralLocationEntity>
   suspend fun updateUnSend()
   suspend fun deleteSent()

   suspend fun sendLocationToServer(liveLocationRequest: List<LiveLocationRequest>)
}
package domain.repository

import database.entity.PhotoEntity
import domain.models.DeletePhotoByComponentIdAndKeyModel
import irancell.nwg.wfm.MR


interface IPhotoRepository {

    suspend fun insert(photoEntity: PhotoEntity)

    suspend fun insertAll(photoEntities: List<PhotoEntity>)

    suspend fun getPhotoListByKey(key: String): List<PhotoEntity>

    suspend fun deleteByKey(key: String)
    suspend fun deleteByComponentIdAndKey(deletePhotoByComponentIdAndKeyModel: DeletePhotoByComponentIdAndKeyModel)

    suspend fun getTicketProcessPhotos(
        ticket_number: String,
        componentKeyList: List<String>
    ): List<PhotoEntity>

    suspend fun getTicketProcessPhotosWithoutSuspends(ticketNumber: String): List<PhotoEntity>

    suspend fun deleteProcessImages(ticket_number: String)

}
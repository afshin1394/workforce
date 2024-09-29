package data

import androidx.compose.animation.core.rememberTransition
import database.AppDatabase
import database.entity.PhotoEntity
import domain.models.DeletePhotoByComponentIdAndKeyModel
import domain.repository.IPhotoRepository
import irancell.nwg.wfm.MR


class PhotoRepositoryImpl(private val db: AppDatabase) : IPhotoRepository {

    override suspend fun insert(photoEntity: PhotoEntity) {

        db.photoDao().insert(photoEntity)
    }

    override suspend fun insertAll(photoEntities: List<PhotoEntity>) {
        db.photoDao().insertAll(photoEntities)
    }


    override suspend fun getPhotoListByKey(ticketNumber: String): List<PhotoEntity> {

        return db.photoDao().selectByComponentKey(ticketNumber = ticketNumber)

    }


    override suspend fun deleteByKey(ticketNumber: String) {
        db.photoDao().deleteByComponentKey(ticketNumber = ticketNumber)

    }

    override suspend fun deleteByComponentIdAndKey(deletePhotoByComponentIdAndKeyModel: DeletePhotoByComponentIdAndKeyModel) {
        db.photoDao().deleteByComponentIdAndKey(componentId=deletePhotoByComponentIdAndKeyModel.componentId,componentKey=deletePhotoByComponentIdAndKeyModel.componentKey)

    }

    override suspend fun getTicketProcessPhotos(
        ticketNumber: String, componentKeyList: List<String>
    ): List<PhotoEntity> {
        return db.photoDao().getPhotosByComponentKeyList(ticketNumber, componentKeyList)
    }

    override suspend fun getTicketProcessPhotosWithoutSuspends(ticketNumber: String): List<PhotoEntity> {
        return db.photoDao().getProcessPhotoByTicketNumber(ticketNumber = ticketNumber)
    }


    override suspend fun deleteProcessImages(ticket_number: String) {
        db.photoDao().deleteProcessPhotoByTicketNumber(ticket_number)
    }


}
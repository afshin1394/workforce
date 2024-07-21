package data

import domain.repository.IPhotoRepository
import irancell.nwg.wfm.db.PhotoEntity
import irancell.nwg.wfm.db.WFMDatabase

class PhotoRepositoryImpl(private val wfmDatabase: WFMDatabase) : IPhotoRepository {

    override suspend fun insert(photoEntity: PhotoEntity) {
        wfmDatabase.photoEntityQueries.insert(
            photoEntity.ticket_number,
            photoEntity.component_key,
            photoEntity.index_row,
            photoEntity.origin_uri,
            photoEntity.edited_uri,
            photoEntity.angle
        )

    }



    override suspend fun getPhotoListByKey(ticketNumber: String): List<PhotoEntity>  {
      return wfmDatabase.photoEntityQueries.selectByComponentKey(ticketNumber).executeAsList()
    }



    override suspend fun deleteByKey(ticketNumber: String) {

        wfmDatabase.photoEntityQueries.deleteByComponentKey(ticketNumber)
    }


}
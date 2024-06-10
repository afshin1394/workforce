package data

import domain.repository.IPhotoRepository
import irancell.nwg.wfm.db.PhotoEntity
import irancell.nwg.wfm.db.WFMDatabase

class PhotoRepositoryImpl(private val wfmDatabase: WFMDatabase) : IPhotoRepository {

    override suspend fun insert(photoEntity: PhotoEntity) {
        wfmDatabase.photoEntityQueries.insert(
            photoEntity.task_id,
            photoEntity.component_key,
            photoEntity.index_row,
            photoEntity.origin_uri,
            photoEntity.edited_uri,
            photoEntity.angle
        )

    }



    override suspend fun getPhotoListByComponentKey(taskId: Long): List<PhotoEntity>  {
      return wfmDatabase.photoEntityQueries.selectByComponentKey(taskId).executeAsList()
    }



    override suspend fun deleteByComponentKey(taskId: Long) {

        wfmDatabase.photoEntityQueries.deleteByComponentKey(taskId)
    }


}
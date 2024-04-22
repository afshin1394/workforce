package data

import domain.repository.IPhotoRepository
import irancell.nwg.wfm.db.PhotoEntity
import irancell.nwg.wfm.db.WFMDatabase

class PhotoRepositoryImpl(private val wfmDatabase: WFMDatabase) : IPhotoRepository {

    override suspend fun insert(photoEntity: PhotoEntity) {
        wfmDatabase.photoEntityQueries.insert(
            photoEntity.component_key,
            photoEntity.index_row,
            photoEntity.origin_uri,
            photoEntity.edited_uri,
            photoEntity.angle
        )

    }



    override suspend fun getPhotoListByComponentKey(component_key: Long): List<PhotoEntity>  {
      return wfmDatabase.photoEntityQueries.selectByComponentKey(component_key).executeAsList()
    }



    override suspend fun deleteByComponentKey(component_key: Long) {

        wfmDatabase.photoEntityQueries.deleteByComponentKey(component_key)
    }


}
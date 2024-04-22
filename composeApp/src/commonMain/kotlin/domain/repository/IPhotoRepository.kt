package domain.repository

import irancell.nwg.wfm.db.PhotoEntity


interface IPhotoRepository {

    suspend fun insert(photoEntity: PhotoEntity)

    suspend fun getPhotoListByComponentKey(component_key: Long): List<PhotoEntity>

    suspend fun deleteByComponentKey(component_key:Long)


}
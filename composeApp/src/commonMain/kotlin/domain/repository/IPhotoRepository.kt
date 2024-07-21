package domain.repository

import irancell.nwg.wfm.db.PhotoEntity


interface IPhotoRepository {

    suspend fun insert(photoEntity: PhotoEntity)

    suspend fun getPhotoListByKey(key: String): List<PhotoEntity>

    suspend fun deleteByKey(key:String)


}
package domain.repository

import database.entity.PhotoEntity


interface IPhotoRepository {

    suspend fun insert(photoEntity: PhotoEntity)

    suspend fun getPhotoListByKey(key: String): List<PhotoEntity>

    suspend fun deleteByKey(key:String)


}
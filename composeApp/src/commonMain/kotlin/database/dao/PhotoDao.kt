package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.PhotoEntity

@Dao
interface PhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("DELETE FROM PhotoEntity WHERE origin_uri = :originUri")
    suspend fun deletePhoto(originUri: String)

    @Query("DELETE FROM PhotoEntity WHERE ticket_number = :ticketNumber")
    suspend fun deleteByComponentKey(ticketNumber: String)

    @Query("SELECT * FROM PhotoEntity WHERE ticket_number = :ticketNumber")
    suspend fun selectByComponentKey(ticketNumber: String): List<PhotoEntity>

    @Query("UPDATE PhotoEntity SET angle = :angle WHERE origin_uri = :originUri")
    suspend fun updateAngle(originUri: String, angle: String)

    @Query("UPDATE PhotoEntity SET edited_uri = :editedUri WHERE origin_uri = :originUri")
    suspend fun updateEditUri(originUri: String, editedUri: String)
}
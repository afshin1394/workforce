package database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.GeneralLocationEntity

@Dao
interface GeneralLocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(generalLocation: GeneralLocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(generalLocations: List<GeneralLocationEntity>)


    @Query("DELETE FROM GeneralLocationEntity WHERE isSent = 1")
    suspend fun deleteAllSent()

    @Query("DELETE FROM GeneralLocationEntity")
    suspend fun deleteAll()


    @Query("UPDATE GeneralLocationEntity SET isSent = 1")
    suspend fun updateAllAsSent()

    @Delete
    suspend fun deleteRecord(generalLocation: GeneralLocationEntity)

    @Query("SELECT * FROM GeneralLocationEntity WHERE isSent = 0 ORDER BY datetime ASC")
    suspend fun selectAllNotSentLocation(): List<GeneralLocationEntity>


    @Query("SELECT * FROM GeneralLocationEntity ORDER BY datetime ASC")
    suspend fun selectAll(): List<GeneralLocationEntity>

    @Query("Select count(*) from GeneralLocationEntity")
    suspend fun selectNumberOfRecords() : Int

    @Query("SELECT * FROM GeneralLocationEntity ORDER BY datetime ASC LIMIT 1")
    suspend fun selectOldestRecord(): GeneralLocationEntity?

}
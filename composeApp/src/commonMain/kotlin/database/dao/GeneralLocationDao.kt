package database.dao

import androidx.room.Dao
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

    @Query("UPDATE GeneralLocationEntity SET isSent = 1")
    suspend fun updateAllAsSent()

    @Query("SELECT * FROM GeneralLocationEntity WHERE isSent = 0 ORDER BY datetime ASC")
    suspend fun selectAllNotSentLocation(): List<GeneralLocationEntity>

    @Query("SELECT * FROM GeneralLocationEntity ORDER BY datetime ASC")
    suspend fun selectAll(): List<GeneralLocationEntity>

    @Query("DELETE FROM GeneralLocationEntity WHERE isSent = 1")
    suspend fun deleteAllSent()
}
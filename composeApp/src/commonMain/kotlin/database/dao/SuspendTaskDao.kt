package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.SuspendTaskEntity

@Dao
interface SuspendTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: SuspendTaskEntity)

    @Query("UPDATE SuspendTaskEntity SET isSent = 1")
    suspend fun updateUnsend()

    @Query("UPDATE SuspendTaskEntity SET attachmentsUri = :attachments WHERE ticket_number = :ticketNumber")
    suspend fun updateAttachments(ticketNumber: String, attachments: String)

    @Query("UPDATE SuspendTaskEntity SET datetime = :dateTime, latitude = :latitude, longitude = :longitude WHERE ticket_number = :ticketNumber")
    suspend fun updateSuspendDetails(ticketNumber: String, dateTime: String, latitude: String, longitude: String)

    @Query("DELETE FROM SuspendTaskEntity WHERE isSent = 1")
    suspend fun deleteAllSent()

    @Query("DELETE FROM SuspendTaskEntity WHERE ticket_number = :ticketNumber")
    suspend fun deleteByTicketNumber(ticketNumber: String)

    @Query("DELETE FROM SuspendTaskEntity WHERE isSent = 1")
    suspend fun removeSentItems()

    @Query("SELECT * FROM SuspendTaskEntity WHERE isSent = 0 ORDER BY datetime ASC")
    suspend fun selectAllNotSent(): List<SuspendTaskEntity>

    @Query("SELECT * FROM SuspendTaskEntity ORDER BY datetime ASC")
    suspend fun selectAll(): List<SuspendTaskEntity>

    @Query("SELECT * FROM SuspendTaskEntity WHERE ticket_number = :ticketNumber")
    suspend fun selectByTaskId(ticketNumber: String): SuspendTaskEntity
}
package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.TaskEntity

@Dao
interface TaskDao {
    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'TaskEntity'")
    suspend fun resetSequence()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(task: List<TaskEntity>)

    @Query("DELETE FROM TaskEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM TaskEntity")
    suspend fun selectAll(): List<TaskEntity>

    @Query("UPDATE TaskEntity SET ticket_state = :ticketState WHERE ticket_number = :ticketNumber")
    suspend fun updateStatus(ticketNumber: String, ticketState: String)
}
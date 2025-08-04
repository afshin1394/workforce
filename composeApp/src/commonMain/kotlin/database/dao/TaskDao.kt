package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.ActivityListEntity
import database.entity.TaskEntity

@Dao
interface TaskDao {

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'TaskEntity'")
    suspend fun resetSequence()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(task: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllActivityList(task: List<ActivityListEntity>)

    @Query("DELETE FROM TaskEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM TaskEntity ORDER BY ticket_id DESC")
    suspend fun selectAll(): List<TaskEntity>

    @Query("SELECT * FROM TaskEntity ORDER BY ticket_id DESC LIMIT :limit OFFSET :offset")
    suspend fun selectPaginated(limit: Int, offset: Int): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM TaskEntity")
    suspend fun getTaskCount(): Int

    @Query("SELECT * FROM TaskEntity WHERE " +
            "ticket_number LIKE '%' || :query || '%' OR " +
            "ticket_state LIKE '%' || :query || '%' OR " +
            "activity__title LIKE '%' || :query || '%' " +
            "ORDER BY ticket_id DESC LIMIT :limit OFFSET :offset")
    suspend fun searchTasksPaginated(query: String, limit: Int, offset: Int): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM TaskEntity WHERE " +
            "ticket_number LIKE '%' || :query || '%' OR " +
            "ticket_state LIKE '%' || :query || '%' OR " +
            "activity__title LIKE '%' || :query || '%'")
    suspend fun getSearchTaskCount(query: String): Int

    @Query("SELECT * FROM ActivityListEntity ")
    suspend fun selectAllActivityList(): List<ActivityListEntity>

    @Query("UPDATE TaskEntity SET ticket_state = :ticketState WHERE ticket_number = :ticketNumber")
    suspend fun updateStatus(ticketNumber: String, ticketState: String)

    @Query("SELECT * FROM TaskEntity WHERE ticket_number = :ticketNumber LIMIT 1")
    suspend fun getTaskByTicketNumber(ticketNumber: String): TaskEntity?

    @Query("DELETE FROM ActivityListEntity")
    suspend fun deleteAllActivityList()

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'ActivityListEntity'")
    suspend fun resetActivityListSequence()

    @Query("DELETE FROM TaskEntity WHERE ticket_number NOT IN (:ticketNumbers)")
    suspend fun deleteAllExcept(ticketNumbers: List<String>)

    @Query("DELETE FROM TaskEntity WHERE ticket_number IN (:ticketNumbers)")
    suspend fun deleteSpecific(ticketNumbers: List<String>)

}
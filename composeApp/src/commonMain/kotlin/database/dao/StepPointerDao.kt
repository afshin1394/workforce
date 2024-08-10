package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.StepPointerEntity

@Dao
interface  StepPointerDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(task: List<StepPointerEntity>)

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'StepPointerEntity'")
    suspend fun resetSequence()

    @Query("DELETE FROM StepPointerEntity WHERE  ticketNumber NOT IN (:editedAvailableTickets)")
    suspend fun deleteAll(editedAvailableTickets : List<String>)

    @Query("SELECT * FROM StepPointerEntity")
    suspend fun selectAll(): List<StepPointerEntity>

    @Query("SELECT * FROM StepPointerEntity WHERE ticketNumber = :ticketNumber LIMIT 1")
    suspend fun selectActiveActivityByTicketNumber(ticketNumber: String) : StepPointerEntity

    @Query("UPDATE StepPointerEntity SET activeActivity = :activeActivity , edited = true WHERE ticketNumber = :ticketNumber")
    suspend fun updateActiveActivity(ticketNumber : String ,activeActivity: Long)

    @Query("UPDATE StepPointerEntity SET edited = :isEdited WHERE ticketNumber = :ticketNumber")
    suspend fun updateIsEdited(ticketNumber : String,isEdited : Boolean )




}

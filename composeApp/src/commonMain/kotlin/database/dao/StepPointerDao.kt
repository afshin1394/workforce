package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.StepPointerEntity

@Dao
interface  StepPointerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(task: List<StepPointerEntity>)

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'StepPointerEntity'")
    suspend fun resetSequence()

    @Query("DELETE FROM StepPointerEntity WHERE  ticketNumber NOT IN (:editedAvailableTickets)")
    suspend fun deleteAll(editedAvailableTickets : List<String>)

    @Query("DELETE FROM StepPointerEntity WHERE  ticketNumber  IN (:editedAvailableTickets)")
    suspend fun deleteAllStepPointers(editedAvailableTickets : List<String>)


    @Query("SELECT * FROM StepPointerEntity")
    suspend fun selectAll(): List<StepPointerEntity>

    @Query("SELECT * FROM StepPointerEntity WHERE ticketNumber = :ticketNumber LIMIT 1")
    suspend fun selectActiveActivityByTicketNumber(ticketNumber: String) : StepPointerEntity

    @Query("UPDATE StepPointerEntity SET activeActivity = :activeActivity , edited = 1 WHERE ticketNumber = :ticketNumber")
    suspend fun updateActiveActivity(ticketNumber : String ,activeActivity: Long)

    @Query("UPDATE StepPointerEntity SET edited = :isEdited WHERE ticketNumber = :ticketNumber")
    suspend fun updateIsEdited(ticketNumber : String,isEdited : Boolean )


    @Query("SELECT * FROM StepPointerEntity WHERE edited = 1")
    suspend fun selectEditedTickets() : List<StepPointerEntity>

    @Query("DELETE FROM StepPointerEntity WHERE ticketNumber NOT IN (:ticketNumbers)")
    suspend fun deleteAllExcept(ticketNumbers: List<String>)

    @Query("DELETE FROM StepPointerEntity WHERE ticketNumber IN (:ticketNumbers)")
    suspend fun deleteSpecific(ticketNumbers: List<String>)

    @Query("SELECT ticketNumber FROM StepPointerEntity WHERE edited = 1")
    suspend fun selectModifiedTicketNumbers(): List<String>

}

package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.StepsEntity


@Dao
interface StepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(task: List<StepsEntity>)

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'StepsEntity'")
    suspend fun resetSequence()

    @Query("DELETE FROM StepsEntity WHERE  ticketNumber NOT IN (:ticketNumbers)")
    suspend fun deleteAll(ticketNumbers : List<String>)

    @Query("SELECT * FROM StepsEntity")
    suspend fun selectAll(): List<StepsEntity>

    @Query("SELECT * FROM StepsEntity WHERE ticketNumber = :ticketNumber  ORDER BY activityId")
    suspend fun selectStepsByTicketNumber(ticketNumber: String) : List<StepsEntity>

    @Query("SELECT ticketNumber FROM StepsEntity WHERE edited = true")
    suspend fun selectEditedTickets() : List<String>

    @Query("UPDATE StepsEntity SET formStructure = :formStructure,photoList =:photoList,isSent = 0 , edited = true WHERE ticketNumber = :ticketNumber AND activityId = :activityId")
    suspend fun updateFormStructure(ticketNumber: String,activityId : Long, formStructure: String,photoList : String)


    @Query("SELECT * FROM StepsEntity WHERE ticketNumber = :ticketNumber AND  activityId = :activityId LIMIT 1")
    suspend fun selectStepsByTicketNumberAndActivityId(ticketNumber: String,activityId : Long) : StepsEntity


}
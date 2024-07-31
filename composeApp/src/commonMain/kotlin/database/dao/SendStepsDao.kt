package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.SendStepsEntity
import database.entity.StepsEntity

@Dao
interface SendStepsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(task: List<SendStepsEntity>)

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'SendStepsEntity'")
    suspend fun resetSequence()

    @Query("DELETE FROM SendStepsEntity WHERE  ticketNumber NOT IN (:ticketNumbers)")
    suspend fun deleteAll(ticketNumbers : List<String>)

    @Query("SELECT * FROM SendStepsEntity")
    suspend fun selectAll(): List<SendStepsEntity>

    @Query("SELECT * FROM SendStepsEntity WHERE ticketNumber = :ticketNumber  ORDER BY activityId")
    suspend fun selectSendStepsByTicketNumber(ticketNumber: String) : List<SendStepsEntity>

    @Query("SELECT ticketNumber FROM SendStepsEntity WHERE edited = true")
    suspend fun selectEditedTickets() : List<String>

    @Query("UPDATE SendStepsEntity SET key_value_structure = :keyValueStructure , edited = true WHERE ticketNumber = :ticketNumber AND activityId = :activityId")
    suspend fun updateKeyValueStructure(ticketNumber: String,activityId : Long, keyValueStructure: String)


    @Query("SELECT * FROM SendStepsEntity WHERE ticketNumber = :ticketNumber AND  activityId = :activityId LIMIT 1")
    suspend fun selectSendStepsByTicketNumberAndActivityId(ticketNumber: String,activityId : Long) : SendStepsEntity

}
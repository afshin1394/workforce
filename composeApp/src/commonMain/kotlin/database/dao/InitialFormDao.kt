package database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import database.entity.InitialFormEntity

@Dao
interface InitialFormDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(initialForm: InitialFormEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(initialForms: List<InitialFormEntity>)

    @Query("DELETE FROM InitialFormEntity")
    suspend fun deleteAll()

    @Query("SELECT * FROM InitialFormEntity")
    suspend fun selectAll(): List<InitialFormEntity>

    @Query("SELECT * FROM InitialFormEntity WHERE ticket_number = :ticketNumber")
    suspend fun selectByTicketNumber(ticketNumber: String): InitialFormEntity?

    @Query("UPDATE InitialFormEntity SET structure = :structure WHERE ticket_number = :ticketNumber")
    suspend fun updateStructure(ticketNumber: String, structure: String)

    @Query("UPDATE sqlite_sequence SET seq = 0 WHERE name = 'InitialFormEntity'")
    suspend fun resetSequence()
}
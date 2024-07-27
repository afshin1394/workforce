package domain.repository

import database.entity.StepPointerEntity
import database.entity.StepsEntity

interface IStepPointerRepository {
    suspend fun insertAll(pointers : List<StepPointerEntity>)
    suspend fun deleteAll()
    suspend fun getActiveActivityByTicketNumber(ticketNumber : String)
    suspend fun resetEntitySequence()
    suspend fun updateActiveActivity(ticketNumber: String)
}
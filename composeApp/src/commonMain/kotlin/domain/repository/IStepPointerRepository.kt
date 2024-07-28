package domain.repository

import database.entity.StepPointerEntity
import database.entity.StepsEntity
import domain.models.steps.StepPointerDomain

interface IStepPointerRepository {
    suspend fun insertAll(pointers : List<StepPointerEntity>)
    suspend fun deleteAll()
    suspend fun getActiveActivityByTicketNumber(ticketNumber : String) : StepPointerDomain
    suspend fun resetEntitySequence()
    suspend fun updateActiveActivity(ticketNumber: String,activeActivity : Long)
}
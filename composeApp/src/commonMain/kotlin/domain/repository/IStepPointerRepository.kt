package domain.repository

import database.entity.StepPointerEntity
import database.entity.StepsEntity
import domain.models.steps.StepPointerDomain

interface IStepPointerRepository {
    suspend fun insertAll(pointers : List<StepPointerEntity>)
    suspend fun deleteAll(editedAvailableTickets : List<String>)
    suspend fun getActiveActivityByTicketNumber(ticketNumber : String) : StepPointerDomain
    suspend fun checkIfTicketIsEdited(ticketNumber: String) : Boolean
    suspend fun resetEntitySequence()
    suspend fun updateActiveActivity(ticketNumber: String,activeActivity : Long)
}
package domain.repository

import data.network.response.task.step.TaskStepResponse
import database.entity.StepsEntity


interface IStepsRepository {
    suspend fun fetch(query : String) : TaskStepResponse
    suspend fun insertAll(tasks : List<StepsEntity>)
    suspend fun deleteAll(ticketNumbers : List<String>)
    suspend fun getStepsByTicketNumber(ticketNumber : String)
    suspend fun getEditedTickets(): List<String>
    suspend fun resetEntitySequence()
}
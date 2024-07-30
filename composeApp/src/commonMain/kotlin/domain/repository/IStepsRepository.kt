package domain.repository

import data.network.response.task.step.TaskStepResponse
import database.entity.StepsEntity
import domain.models.steps.ActivityDomain
import domain.models.steps.StepDetailDomain


interface IStepsRepository {
    suspend fun fetch(query : String) : TaskStepResponse
    suspend fun insertAll(tasks : List<StepsEntity>)
    suspend fun deleteAll(ticketNumbers : List<String>)
    suspend fun getStepsByTicketNumber(ticketNumber : String) : List<StepsEntity>
    suspend fun getDataByTicketNumberAndStep(ticketNumber : String,activityId : Long) : StepsEntity
    suspend fun getEditedTickets(): List<String>
    suspend fun resetEntitySequence()
    suspend fun updateFormStructure(ticketNumber: String,activityId: Long,formStructure : String)
}
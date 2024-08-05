package domain.repository

import data.network.request.step.SubmitAllRequest
import database.entity.SendStepsEntity
import database.entity.StepPointerEntity
import database.entity.StepsEntity

interface ISendStepsRepository {
    suspend fun sendData(json: String)
    suspend fun insertAll(tasks : List<SendStepsEntity>)
    suspend fun deleteAll(ticketNumbers : List<String>)
    suspend fun getStepsByTicketNumber(ticketNumber : String) : List<SendStepsEntity>
    suspend fun getDataByTicketNumberAndStep(ticketNumber : String,activityId : Long) : SendStepsEntity
    suspend fun getEditedTickets(): List<String>
    suspend fun resetEntitySequence()
    suspend fun updateKeyValueStructure(ticketNumber: String,activityId: Long,keyValueStructure : String)
}
package data

import data.network.request.step.SubmitAllRequest
import database.AppDatabase
import database.entity.SendStepsEntity
import domain.repository.ISendStepsRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class SendStepRepositoryImpl(private val db : AppDatabase,private val httpClient: HttpClient) : ISendStepsRepository  {
    override suspend fun sendData(json: String) {
        httpClient.post("workforce_management/user/v1/submit-all/"){
            setBody(json)
        }
    }

    override suspend fun insertAll(tasks: List<SendStepsEntity>) {
        db.sendStepsDao().insertAll(tasks)

    }

    override suspend fun deleteAll(ticketNumbers: List<String>) {
        db.sendStepsDao().deleteAll(ticketNumbers)
    }

    override suspend fun deleteAllSendSteps(ticketNumbers: List<String>) {
        db.sendStepsDao().deleteAllSendSteps(ticketNumbers)
    }

    override suspend fun getStepsByTicketNumber(ticketNumber: String): List<SendStepsEntity> {
       return db.sendStepsDao().selectSendStepsByTicketNumber(ticketNumber)
    }

    override suspend fun getDataByTicketNumberAndStep(
        ticketNumber: String,
        activityId: Long
    ): SendStepsEntity {
       return db.sendStepsDao().selectSendStepsByTicketNumberAndActivityId(ticketNumber,activityId)
    }

    override suspend fun getEditedTickets(): List<String> {
      return  db.sendStepsDao().selectEditedTickets()
    }

    override suspend fun resetEntitySequence() {
        db.sendStepsDao().resetSequence()
    }

    override suspend fun updateKeyValueStructure(
        ticketNumber: String,
        activityId: Long,
        keyValueStructure: String,
        keyValueImageStructure : String,
    ) {
        db.sendStepsDao().updateKeyValueStructure(ticketNumber,activityId,keyValueStructure,keyValueImageStructure)
    }
}
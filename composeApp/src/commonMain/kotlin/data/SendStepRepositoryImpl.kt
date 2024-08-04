package data

import database.AppDatabase
import database.entity.SendStepsEntity
import domain.repository.ISendStepsRepository

class SendStepRepositoryImpl(private val db : AppDatabase) : ISendStepsRepository  {
    override suspend fun insertAll(tasks: List<SendStepsEntity>) {
        db.sendStepsDao().insertAll(tasks)

    }

    override suspend fun deleteAll(ticketNumbers: List<String>) {
        db.sendStepsDao().deleteAll(ticketNumbers)
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
        keyValueStructure: String
    ) {
        db.sendStepsDao().updateKeyValueStructure(ticketNumber,activityId,keyValueStructure)
    }
}
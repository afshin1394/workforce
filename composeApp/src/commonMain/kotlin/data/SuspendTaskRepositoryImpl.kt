package data

import database.AppDatabase
import database.entity.SuspendTaskEntity
import domain.repository.ISuspendTaskRepository
import io.ktor.client.HttpClient


class SuspendTaskRepositoryImpl(
    private val db: AppDatabase,
    private val httpClient: HttpClient
) : ISuspendTaskRepository {
    override suspend fun insert(suspendTask: SuspendTaskEntity) {

        db.suspendTaskDao().insert(suspendTask)

    }

    override suspend fun updateUnSend() {

        db.suspendTaskDao().updateUnsend()


    }

    override suspend fun updateAttachments(attachments : String,ticketNumber: String) {
        db.suspendTaskDao().updateAttachments(attachments=attachments, ticketNumber = ticketNumber)

    }

    override suspend fun updateSuspendDetails(ticketNumber: String,dateTime : String,latitude : String,longitude : String) {

        db.suspendTaskDao().updateSuspendDetails(ticketNumber=ticketNumber,dateTime=dateTime,latitude=latitude,longitude=longitude)


    }

    override suspend fun deleteSent() {

        db.suspendTaskDao().deleteAllSent()

    }

    override suspend fun deleteByTaskId(ticketNumber: String) {
        db.suspendTaskDao().deleteByTicketNumber(ticketNumber=ticketNumber)

    }

    override suspend fun selectUnSend(): List<SuspendTaskEntity> {


        return db.suspendTaskDao().selectAllNotSent()

    }

    override suspend fun selectByTaskId(ticketNumber: String): SuspendTaskEntity {

        return db.suspendTaskDao().selectByTaskId(ticketNumber=ticketNumber)

    }

    override suspend fun sendSuspendedTask(ticketNumber: String) {
    }

    override suspend fun sendNotSendSuspendedTasks() {

    }
}
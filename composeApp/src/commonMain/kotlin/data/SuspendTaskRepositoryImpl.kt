package data

import domain.repository.ISuspendTaskRepository
import io.ktor.client.HttpClient
import irancell.nwg.wfm.db.SuspendTaskEntity
import irancell.nwg.wfm.db.WFMDatabase

class SuspendTaskRepositoryImpl(
    private val wfmDatabase: WFMDatabase,
    private val httpClient: HttpClient
) : ISuspendTaskRepository {
    override suspend fun insert(suspendTask: SuspendTaskEntity) {
        wfmDatabase.suspendTaskEntityQueries.insert(
            suspendTask.ticket_number,
            suspendTask.reason,
            suspendTask.description,
            suspendTask.attachmentsUri,
            suspendTask.isSent,
            suspendTask.datetime,
            suspendTask.latitude,
            suspendTask.longitude
        )
    }

    override suspend fun updateUnSend() {
        wfmDatabase.suspendTaskEntityQueries.updateUnsend()

    }

    override suspend fun updateAttachments(attachments : String,ticketNumber: String) {
        wfmDatabase.suspendTaskEntityQueries.updateAttachements(attachments, ticket_number = ticketNumber)
    }

    override suspend fun updateSuspendDetails(ticketNumber: String,dateTime : String,latitude : String,longitude : String) {
        wfmDatabase.suspendTaskEntityQueries.updateSuspendDetails(dateTime,latitude,longitude, ticket_number = ticketNumber)

    }

    override suspend fun deleteSent() {
       wfmDatabase.suspendTaskEntityQueries.deleteAllSent()
    }

    override suspend fun deleteByTaskId(ticketNumber: String) {
        wfmDatabase.suspendTaskEntityQueries.deleteByTicketNumber(ticketNumber)
    }

    override suspend fun selectUnSend(): List<SuspendTaskEntity> {
     return  wfmDatabase.suspendTaskEntityQueries.selectAllNotSent().executeAsList()
    }

    override suspend fun selectByTaskId(ticketNumber: String): SuspendTaskEntity {
        return wfmDatabase.suspendTaskEntityQueries.selectByTaskId(ticketNumber).executeAsOne()
    }

    override suspend fun sendSuspendedTask(ticketNumber: String) {
    }

    override suspend fun sendNotSendSuspendedTasks() {

    }
}
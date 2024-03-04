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
            suspendTask.taskId,
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

    override suspend fun updateAttachments(attachments : String,taskId: Int) {
        wfmDatabase.suspendTaskEntityQueries.updateAttachements(attachments,taskId.toLong())
    }

    override suspend fun updateSuspendDetails(taskId: Int,dateTime : String,latitude : String,longitude : String) {
        wfmDatabase.suspendTaskEntityQueries.updateSuspendDetails(dateTime,latitude,longitude,taskId.toLong())

    }

    override suspend fun deleteSent() {
       wfmDatabase.suspendTaskEntityQueries.deleteAllSent()
    }

    override suspend fun deleteByTaskId(taskId: Long) {
        wfmDatabase.suspendTaskEntityQueries.deleteByTaskId(taskId)
    }

    override suspend fun selectUnSend(): List<SuspendTaskEntity> {
     return  wfmDatabase.suspendTaskEntityQueries.selectAllNotSent().executeAsList()
    }

    override suspend fun selectByTaskId(taskId: Long): SuspendTaskEntity {
        return wfmDatabase.suspendTaskEntityQueries.selectByTaskId(taskId).executeAsOne()
    }

    override suspend fun sendSuspendedTask(taskId: Int) {
    }

    override suspend fun sendNotSendSuspendedTasks() {

    }
}
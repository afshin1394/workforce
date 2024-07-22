package domain.repository

import database.entity.SuspendTaskEntity


interface ISuspendTaskRepository {
    suspend fun insert(suspendTask: SuspendTaskEntity)

    suspend fun updateUnSend()
    suspend fun updateAttachments(attachments : String,ticketNumber: String)
    suspend fun updateSuspendDetails(ticketNumber: String,dateTime : String,latitude : String,longitude : String)

    suspend fun deleteSent()
    suspend fun deleteByTaskId(ticketNumber: String)

    suspend fun selectUnSend(): List<SuspendTaskEntity>
    suspend fun selectByTaskId(ticketNumber: String) : SuspendTaskEntity

    suspend fun sendSuspendedTask(ticketNumber: String)
    suspend fun sendNotSendSuspendedTasks()

}
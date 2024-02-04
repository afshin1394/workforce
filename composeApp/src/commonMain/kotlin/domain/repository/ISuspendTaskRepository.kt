package domain.repository

import irancell.nwg.wfm.db.SuspendTaskEntity


interface ISuspendTaskRepository {
    suspend fun insert(suspendTask: SuspendTaskEntity)

    suspend fun updateUnSend()
    suspend fun updateAttachments(attachments : String,taskId: Int)
    suspend fun updateSuspendDetails(taskId : Int,dateTime : String,latitude : String,longitude : String)

    suspend fun deleteSent()

    suspend fun selectUnSend(): List<SuspendTaskEntity>
    suspend fun selectByTaskId(taskId: Long) : SuspendTaskEntity

    suspend fun sendSuspendedTask(taskId: Int)
    suspend fun sendNotSendSuspendedTasks()

}
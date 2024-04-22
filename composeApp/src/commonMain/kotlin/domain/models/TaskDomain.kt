package domain.models

import kotlinx.serialization.Serializable

@Serializable
data class TaskDomain(
    val workId: Long,
    val instanceId: Long,
    val title: String,
    val instanceNumber: String,
    val instanceState: String,
    val instanceStateId : Int,
    val instanceTitle: String,
    val status: String
){
    override fun toString(): String {
        return "TaskDomain(workId=$workId, instanceId=$instanceId, title='$title', instanceNumber='$instanceNumber', instanceState='$instanceState', instanceStateId=$instanceStateId, instanceTitle='$instanceTitle', status='$status')"
    }
}

package data.network.response.download

import data.network.response.task.FormStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ChunkResponse(
    @SerialName("job_id")
    val jobId: String? = null,
    @SerialName("part_id")
    val partId: Int? = null,
    @SerialName("total_parts")
    val totalParts: Int? = null,
    @SerialName("data")
    val data: List<TaskChunkData> = emptyList(),
    // Status fields for pending/processing responses
    @SerialName("status")
    val status: String? = null,
    @SerialName("message")
    val message: String? = null,
    // Error handling
    @SerialName("error")
    val error: String? = null
) {
    fun isPending(): Boolean = status?.lowercase() == "pending"
    fun isReady(): Boolean = status?.lowercase() == "ready" || (jobId != null && partId != null && totalParts != null)
    fun hasError(): Boolean = error != null
}

@Serializable
data class TaskChunkData(
    @SerialName("id")
    val id: Long,
    @SerialName("instance__tickets__number")
    val instanceTicketsNumber: String,
    @SerialName("instance__tickets__properties")
    val instanceTicketsProperties: List<TaskPropertyData>? = null,
    @SerialName("instance__tickets__id")
    val instanceTicketsId: Long,
    @SerialName("instance__tickets__ticket_id")
    val instanceTicketsTicketId: Long,
    @SerialName("instance__tickets__state")
    val instanceTicketsState: String,
    @SerialName("instance__tickets__basic_information__values")
    val instanceTicketsBasicInformationValues: List<TaskPropertyData>? = null,
    @SerialName("activity_id")
    val activityId: Long,
    @SerialName("activity__title")
    val activityTitle: String,
    @SerialName("init_structure")
    val initStructure: JsonElement? = null,
    @SerialName("steps")
    val steps: List<ChunkStepData>? = null
)

@Serializable
data class ChunkStepData(
    @SerialName("activity_id")
    val activityId: Long,
    @SerialName("activity_title")
    val activityTitle: String,
    @SerialName("activity_process_id")
    val activityProcessId: Long,
    @SerialName("activity_task_group")
    val activityTaskGroup: String,
    @SerialName("activity_kind")
    val activityKind: String,
    @SerialName("activity_form")
    val activityForm: Long,
    @SerialName("workflow_activity_tags_id")
    val workflowActivityTagsId: Long,
    @SerialName("form_name")
    val formName: String,
    @SerialName("form_structure")
    val formStructure: FormStruct? = null
)

@Serializable
data class TaskPropertyData(
    @SerialName("key")
    val key: String,
    @SerialName("values")
    val values: String
)
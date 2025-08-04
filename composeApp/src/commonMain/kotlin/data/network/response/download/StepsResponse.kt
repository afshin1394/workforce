package data.network.response.download

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StepsResponse(
    @SerialName("steps")
    val steps: List<StepData>
)

@Serializable
data class StepData(
    @SerialName("step_id")
    val stepId: String,
    @SerialName("ticket_number")
    val ticketNumber: String,
    @SerialName("wi")
    val wi: Long? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("tag")
    val tag: Long? = null,
    @SerialName("activity_id")
    val activityId: Long? = null,
    @SerialName("form_structure")
    val formStructure: String? = null,
    @SerialName("step_name")
    val stepName: String? = null,
    @SerialName("step_order")
    val stepOrder: Int? = null,
    @SerialName("step_status")
    val stepStatus: String? = null,
    @SerialName("form_data")
    val formData: String? = null
)
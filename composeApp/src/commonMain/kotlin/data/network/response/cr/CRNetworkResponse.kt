package data.network.response.cr

import data.network.response.task.TaskInstanceAction
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CRNetworkResponse(
    @SerialName("wi_id")
    val wi_id: Long,
    @SerialName("activity_id")
    val activity_id: Long,
    @SerialName("activity_title")
    val activity_title: String,
    @SerialName("activity_state")
    val activity_state: String,
    @SerialName("ticket_title")
    val ticket_title: String,
    @SerialName("ticket_instance_id")
    val ticket_instance_id: Long,
    @SerialName("ticket_instance_number")
    val ticket_instance_number: String,
    @SerialName("ticket_instance_state")
    val ticket_instance_state: String,
    @SerialName("ticket_instance_title")
    val ticket_instance_title: String,
    @SerialName("ticket_instance_action")
    val ticket_instance_action: TaskInstanceAction? = null,
)

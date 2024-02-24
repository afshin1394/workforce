package data.network.response.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TasksNetworkResponse(
    @SerialName("wi_id")
    val wi_id: Long,
    @SerialName("ticket_instance_id")
    val ticket_instance_id: Long ,
    @SerialName("ticket_title")
    val ticket_title: String?= null,
    @SerialName("ticket_instance_number")
    val ticket_instance_number: String?= null,
    @SerialName("ticket_instance_state")
    val ticket_instance_state: String?= null,
    @SerialName("ticket_instance_title")
    val ticket_instance_title: String?= null,
    @SerialName("initial_form")
    val initial_form : InitialForm?= null,
    @SerialName("ticket_instance_action")
    val ticket_instance_action : TaskInstanceAction? = null,
){
    override fun toString(): String {
        return "TasksNetworkResponse(wi_id=$wi_id, ticket_instance_id=$ticket_instance_id, ticket_title=$ticket_title, ticket_instance_number=$ticket_instance_number, ticket_instance_state=$ticket_instance_state, ticket_instance_title=$ticket_instance_title, initial_form=$initial_form, ticket_instance_action=$ticket_instance_action)"
    }
}

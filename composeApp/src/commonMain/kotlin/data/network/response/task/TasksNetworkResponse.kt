package data.network.response.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TasksNetworkResponse(
    val wi_id: Long,
    val ticket_instance_id: Long ,
    val ticket_title: String?= null,
    val ticket_instance_number: String?= null,
    val ticket_instance_state: String?= null,
    val ticket_instance_title: String?= null,
    val initial_form : InitialForm?= null,
    val ticket_instance_action : TaskInstanceAction? = null,
){
    override fun toString(): String {
        return "TasksNetworkResponse(wi_id=$wi_id, ticket_instance_id=$ticket_instance_id, ticket_title=$ticket_title, ticket_instance_number=$ticket_instance_number, ticket_instance_state=$ticket_instance_state, ticket_instance_title=$ticket_instance_title, initial_form=$initial_form, ticket_instance_action=$ticket_instance_action)"
    }
}

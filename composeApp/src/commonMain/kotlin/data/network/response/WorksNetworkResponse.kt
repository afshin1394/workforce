package data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class WorksNetworkResponse(val wi_id : Int, val ticket_instance_id : Int, val ticket_title : String, val ticket_instance_number : String, val ticket_instance_state : String, val ticket_instance_title : String)

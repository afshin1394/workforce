package data.network.request.ticket

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketDetailRequest(
    @SerialName("phases")
    val phase : List<Phase>
)

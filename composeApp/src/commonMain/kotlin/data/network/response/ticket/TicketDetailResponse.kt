package data.network.response.ticket

import kotlinx.serialization.Serializable

@Serializable
data class TicketDetailResponse(
    val events: Map<String, Objects>
)
@Serializable
data class Objects(
    val attributes: Map<String, String?>
)


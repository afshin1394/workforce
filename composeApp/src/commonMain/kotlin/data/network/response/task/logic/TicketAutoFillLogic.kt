package data.network.response.task.logic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TicketAutoFillLogic(
    @SerialName("options")
    val options : List<TicketAutoFillOption>
    )

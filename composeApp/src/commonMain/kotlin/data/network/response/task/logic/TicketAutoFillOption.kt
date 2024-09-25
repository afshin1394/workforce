package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class TicketAutoFillOption(
    val phaseName: String? = null,
    val property: String? = null,
    val condition_key: String? = null,
    val condition_value: String? = null
)

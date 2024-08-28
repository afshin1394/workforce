package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class TicketAutoFillLogicDomain(
    val phaseName : String?,
    val phase : String?,
    val property : String?,
    val condition_key : String?,
    val condition_field : String?,
    val condition_value : String?)

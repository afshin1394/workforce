package data.network.response.task.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable

@Serializable
data class TicketAutoFillLogicDomain(
    val phaseName : String?,
    val phase : String?,
    val property : String?,
    val condition_key : String?,
    val condition_field : String?,
    val condition_value : String?) 

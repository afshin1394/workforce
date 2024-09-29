package domain.models.form_struct.logic

import kotlinx.serialization.Serializable

@Serializable
data class TicketAutoFillOptionDomain(
    val phaseName : String?,
    val property : String?,
    val condition_key : String?,
    val condition_value : String?
)

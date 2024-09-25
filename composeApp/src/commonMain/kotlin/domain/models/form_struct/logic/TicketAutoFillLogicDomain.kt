package data.network.response.task.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import domain.models.form_struct.logic.TicketAutoFillOptionDomain
import kotlinx.serialization.Serializable

@Serializable
data class TicketAutoFillLogicDomain(
    val options : List<TicketAutoFillOptionDomain>
   )

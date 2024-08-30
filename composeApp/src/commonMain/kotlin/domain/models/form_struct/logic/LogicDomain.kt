package data.network.response.task.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import domain.models.form_struct.logic.ExpressionDomain
import kotlinx.serialization.Serializable
@Serializable
data class LogicDomain(
    val feild: String?=null,
    val logicType: String? = null,
    val experssions: List<ExpressionDomain>? = null,
    val filterOptionsLogic :  List<FilterOptionsLogicDomain>?=null,
    val autoFillLogicDomain : AutoFillLogicDomain?=null,
    val bind_logic : BindLogicDomian?=null,
    val ticketAutoFillLogicDomain : TicketAutoFillLogicDomain?=null
    ) 

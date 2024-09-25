package data.network.response.task.logic

import data.network.response.task.Expression
import kotlinx.serialization.Serializable
@Serializable
data class Logic(
    val logicType: String? = null,
    val experssions: List<Expression>? = null,
    val filterOptionsLogic :  List<FilterOptionsLogic>?=null,
    val autoFillLogic : AutoFillLogic?=null,
    val bind_logic : BindLogic?=null,
    val ticketAutoFillLogic : TicketAutoFillLogic?=null
    )

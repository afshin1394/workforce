package data.network.response.task.task

import data.network.response.task.Expression
import data.network.response.task.logic.AutoFillLogic
import data.network.response.task.logic.BindLogic
import data.network.response.task.logic.FilterOptionsLogic
import data.network.response.task.logic.TicketAutoFillLogic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InitLogic(
    @SerialName("logicType")
    val logicType: String? = null,
    @SerialName("experssions")
    val experssions: List<InitExpression>? = null,
  /*  @SerialName("filterOptionsLogics")
   val filterOptionsLogic :  List<InitFilterOptionsLogic>?=null,*/

/* @SerialName("autoFillLogic")
   val autoFillLogic : InitAutoFillLogic?=null,*/
/*   @SerialName("bind_logic")
   val bind_logic : InitBindLogic?=null,
    @SerialName("ticketAutoFillLogic")
    val ticketAutoFillLogic : TicketAutoFillLogic?=null*/
)
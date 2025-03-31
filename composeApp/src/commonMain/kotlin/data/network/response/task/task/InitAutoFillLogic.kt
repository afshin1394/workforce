package data.network.response.task.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class InitAutoFillLogic(
//    @SerialName("api")
//    val api : String?=null,
    @SerialName("apiName")
    val apiName : String?=null,
//    @SerialName("filterField")
//    val filterField : String?=null,
    @SerialName("filterFieldKey")
    val filterFieldKey : String?=null,
    @SerialName("filterParameter")
    val filterParameter : String?=null,
    @SerialName("property")
    val property : String?=null
)
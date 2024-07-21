package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class AutoFillLogic(
    val api : String?=null,
    val apiName : String?=null,
    val filterField : String?=null,
    val filterFieldKey : String?=null,
    val filterParameter : String?=null,
    val property : String?=null
    )


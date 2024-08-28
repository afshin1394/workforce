package data.network.response.task.logic

import kotlinx.serialization.Serializable

data class AutoFillLogicDomain(
    val api : String?,
    val apiName : String?,
    val filterField : String?,
    val filterFieldKey : String?,
    val filterParameter : String?,
    val property : String?
    )


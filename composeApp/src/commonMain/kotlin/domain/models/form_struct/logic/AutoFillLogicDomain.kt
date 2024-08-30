package data.network.response.task.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable
@Serializable
data class AutoFillLogicDomain(
    val api : String?,
    val apiName : String?,
    val filterField : String?,
    val filterFieldKey : String?,
    val filterParameter : String?,
    val property : String?
    ) 


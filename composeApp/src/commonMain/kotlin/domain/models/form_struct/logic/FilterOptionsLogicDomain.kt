package data.network.response.task.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable

@Serializable
data class FilterOptionsLogicDomain(
    val conditions : List<LogicConditionDomain>?,
    val filteredOptions : List<String>?,
    val multiSelectValues : List<String>?,
    val selectedKeyValues : List<String>?) 


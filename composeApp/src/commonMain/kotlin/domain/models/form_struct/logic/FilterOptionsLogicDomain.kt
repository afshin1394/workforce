package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class FilterOptionsLogicDomain(
    val conditions : List<LogicConditionDomain>?,
    val filteredOptions : List<String>?,
    val multiSelectValues : List<String>?,
    val selectedKeyValues : List<String>?)


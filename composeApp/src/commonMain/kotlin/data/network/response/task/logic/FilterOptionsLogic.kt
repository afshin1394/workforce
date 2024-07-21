package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class FilterOptionsLogic(
    val conditions : List<LogicCondition>?=null,
    val filteredOptions : List<String>?=null,
    val multiSelectValues : List<String>?=null,
    val selectedKeyValues : List<String>?=null)


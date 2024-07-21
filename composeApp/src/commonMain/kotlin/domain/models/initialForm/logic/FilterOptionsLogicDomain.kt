package data.network.response.task.logic

data class FilterOptionsLogicDomain(
    val conditions : List<LogicConditionDomain>?,
    val filteredOptions : List<String>?,
    val multiSelectValues : List<String>?,
    val selectedKeyValues : List<String>?)


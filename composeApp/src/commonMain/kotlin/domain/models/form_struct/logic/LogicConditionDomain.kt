package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class LogicConditionDomain(
    val title : String,
    val firstField : String,
    val secondField : String,
    val secondFieldKey : String,
    val firstOperator : String,
    val secondOperator : String,
    val value : String,
    val values : List<String>,
    val multiSelectValues : List<String>,
    val selectedKeyValues : List<String>,
    val filteredOptions : List<String>,
    val filterParameter : String,
    val apiFilterOptionValue : String,
)

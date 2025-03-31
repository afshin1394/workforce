package data.network.response.task.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class InitLogicCondition(
    @SerialName("title")
    val title : String,
    @SerialName("firstField")
    val firstField : String,
    @SerialName("secondField")
    val secondField : String,
    @SerialName("secondFieldKey")
    val secondFieldKey : String,
    @SerialName("firstOperator")
    val firstOperator : String,
    @SerialName("secondOperator")
    val secondOperator : String,
    @SerialName("value")
    val value : String,
    @SerialName("values")
    val values : List<String>,
    @SerialName("multiSelectValues")
    val multiSelectValues : List<String>,
    @SerialName("selectedKeyValues")
    val selectedKeyValues : List<String>,
    @SerialName("filteredOptions")
    val filteredOptions : List<String>,
    @SerialName("filterParameter")
    val filterParameter : String,
    @SerialName("apiFilterOptionValue")
    val apiFilterOptionValue : String,
)
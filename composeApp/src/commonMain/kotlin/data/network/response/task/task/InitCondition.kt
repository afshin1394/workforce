package data.network.response.task.task

import data.network.response.task.Operator
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitCondition(
    @SerialName("firstFieldKey")
    val firstFieldKey: String? = null,
    @SerialName("secondFieldKey")
    val secondFieldKey: String? = null,
    @SerialName("firstOperator")
    val firstOperator: InitOperator? = null,
    @SerialName("secondOperator")
    val secondOperator: InitOperator? = null,
    @SerialName("values")
    val values: List<String>? = null,
    @SerialName("value")
    val value: String? = null
)
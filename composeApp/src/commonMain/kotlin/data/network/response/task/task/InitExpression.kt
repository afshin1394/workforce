package data.network.response.task.task

import data.network.response.task.Condition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable




@Serializable
data class InitExpression(

    @SerialName("conditions")
    val conditions : List<InitCondition>?= null)
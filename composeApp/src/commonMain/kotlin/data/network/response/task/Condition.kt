package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Condition(val firstFieldKey : String?= null,val secondOperator : Operator?= null,val value : String?= null)

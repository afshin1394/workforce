package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Condition(val firstFieldKey : String?= null,val secondFieldKey : String?= null,val firstOperator : Operator? = null,val secondOperator : Operator?= null,val values : List<String>?= null,val value : String?= null)

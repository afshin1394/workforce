package domain.models.initialForm

import data.network.response.task.Operator
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDomain(val firstFieldKey : String?= null, val secondOperator : OperatorDomain?= null, val value : String?= null)

package domain.models.initialForm

import data.network.response.task.Condition
import kotlinx.serialization.Serializable

@Serializable
data class ExpressionDomain(val conditions : List<ConditionDomain>?= null)

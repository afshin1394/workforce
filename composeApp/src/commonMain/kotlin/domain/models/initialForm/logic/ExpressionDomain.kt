package domain.models.initialForm.logic

import kotlinx.serialization.Serializable

@Serializable
data class ExpressionDomain(val conditions : List<ConditionDomain>?= null)

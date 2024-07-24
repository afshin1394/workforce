package domain.models.form_struct.logic

import kotlinx.serialization.Serializable

@Serializable
data class ExpressionDomain(val conditions : List<ConditionDomain>?= null)

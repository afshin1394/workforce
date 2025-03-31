package domain.models.form_struct.logic

import kotlinx.serialization.Serializable

@Serializable
data class InitLogicDomain (
    val logicType: String? = null,
    val experssions: List<ExpressionDomain>? = null,
)
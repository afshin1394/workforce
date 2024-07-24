package domain.models.form_struct

import kotlinx.serialization.Serializable

@Serializable
data class ConditionDomain(val firstFieldKey : String?= null, val secondOperator : OperatorDomain?= null, val value : String?= null)

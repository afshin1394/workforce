package domain.models.form_struct

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDomain(val firstFieldKey : String?= null, val secondOperator : OperatorDomain?= null, val value : String?= null) 

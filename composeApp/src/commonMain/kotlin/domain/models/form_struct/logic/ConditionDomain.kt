package domain.models.form_struct.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import domain.models.form_struct.OperatorDomain
import kotlinx.serialization.Serializable

@Serializable
data class ConditionDomain(val firstFieldKey : String?= null,val secondFieldKey : String?= null,val firstOperator : OperatorDomain? = null,val secondOperator : OperatorDomain?= null,val values : List<String>?= null,val value : String?= null)

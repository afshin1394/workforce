package domain.models.form_struct

import domain.models.form_struct.logic.ExpressionDomain
import kotlinx.serialization.Serializable

@Serializable
data class LogicDomain(val logicType : String?= null,val experssions : List<ExpressionDomain>?= null)

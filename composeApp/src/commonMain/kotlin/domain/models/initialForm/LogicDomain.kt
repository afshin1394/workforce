package domain.models.initialForm

import domain.models.initialForm.logic.ExpressionDomain
import kotlinx.serialization.Serializable

@Serializable
data class LogicDomain(val logicType : String?= null,val experssions : List<ExpressionDomain>?= null)

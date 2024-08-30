package domain.models.form_struct.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable

@Serializable
data class ExpressionDomain(val conditions : List<ConditionDomain>?= null) 

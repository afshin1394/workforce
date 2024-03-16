package domain.models.initialForm

import kotlinx.serialization.Serializable

@Serializable
data class OperatorDomain(val title : String?= null,val symbol : String?= null)

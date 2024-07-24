package domain.models.form_struct

import kotlinx.serialization.Serializable

@Serializable
data class OperatorDomain(val title : String?= null,val symbol : String?= null)

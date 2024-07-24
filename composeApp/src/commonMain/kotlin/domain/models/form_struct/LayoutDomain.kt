package domain.models.form_struct

import kotlinx.serialization.Serializable

@Serializable
data class LayoutDomain(val row : String?= null,val columns : String?= null)

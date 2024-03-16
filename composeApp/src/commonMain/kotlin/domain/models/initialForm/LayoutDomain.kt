package domain.models.initialForm

import kotlinx.serialization.Serializable

@Serializable
data class LayoutDomain(val row : String?= null,val columns : String?= null)

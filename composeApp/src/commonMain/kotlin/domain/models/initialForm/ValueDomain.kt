package domain.models.initialForm

import kotlinx.serialization.Serializable

@Serializable
data class ValueDomain(
    val label : String?= null,val value : String?= null
)

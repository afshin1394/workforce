package domain.models.initialForm

import kotlinx.serialization.Serializable

@Serializable
data class InitialFormDomain(
     val wi_id : Long,
     val structure : InitialFormStructureDomain
)

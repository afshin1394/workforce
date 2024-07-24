package domain.models.steps

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class StepDetailDomain(
    val init_wi : Int,
    val acitivities : List<ActivityDomain>
)

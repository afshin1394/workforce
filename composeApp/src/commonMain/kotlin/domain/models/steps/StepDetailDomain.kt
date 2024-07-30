package domain.models.steps

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class StepDetailDomain(
    val init_wi : Long,
    val acitivities : List<ActivityDomain?>
)

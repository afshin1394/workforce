package data.network.response.task.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StepDetail(
    @SerialName("init_wi")
    val init_wi : Long,
    @SerialName("activity")
    val acitivities : List<Activity>
)

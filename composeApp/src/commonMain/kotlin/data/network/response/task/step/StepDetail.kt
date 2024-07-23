package data.network.response.task.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StepDetail(
    @SerialName("init_wi")
    val init_wi : Int,
    @SerialName("activity")
    val acitivities : List<Activity>
)

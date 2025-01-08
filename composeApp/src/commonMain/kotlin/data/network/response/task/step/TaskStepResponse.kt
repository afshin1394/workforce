package data.network.response.task.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskStepResponse(
    @SerialName("detail")
    val detail : List<StepDetail>
){
    override fun toString(): String {
        return "$detail )"
    }

}

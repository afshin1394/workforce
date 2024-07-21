package data.network.response.task.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TasksNetworkResponse(
    @SerialName("detail")
    val details : List<Detail>,
){
    override fun toString(): String {
        return "TasksNetworkResponse(details=$details)"
    }
}

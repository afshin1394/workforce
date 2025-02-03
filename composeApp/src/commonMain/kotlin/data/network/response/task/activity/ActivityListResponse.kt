package data.network.response.task.activity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ActivityListResponse(
    @SerialName("id")
    val id: Long? = 0,
    @SerialName("title")
    val title: String? = null,
)

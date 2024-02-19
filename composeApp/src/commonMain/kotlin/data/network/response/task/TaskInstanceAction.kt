package data.network.response.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskInstanceAction(
    @SerialName("type")
    val type: String?=null,
    @SerialName("description")
    val description: String?=null,
    @SerialName("category")
    val category: String?=null,
    @SerialName("subCategory")
    val subCategory: String?=null,
    @SerialName("attachment")
    val attachment: String?=null
)
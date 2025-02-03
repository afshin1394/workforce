package data.network.response.task.typeTask
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class TicketAllMiniResponse(
    @SerialName("pk")
    val pk: Int,
    @SerialName("title")
    val title: String,
    @SerialName("process_id")
    val processId: Int,
    @SerialName("instance_prefix")
    val instancePrefix: String
)

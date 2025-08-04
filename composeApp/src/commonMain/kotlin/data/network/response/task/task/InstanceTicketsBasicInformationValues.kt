package data.network.response.task.task


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InstanceTicketsBasicInformationValues(
    @SerialName("key")
    val key : String,
    @SerialName("values")
    val value : String,

)



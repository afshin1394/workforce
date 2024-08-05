package data.network.response.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadNetworkResponse(
    @SerialName("key")
    var key: String? = null,
    @SerialName("value")
    var value: String? = null
)
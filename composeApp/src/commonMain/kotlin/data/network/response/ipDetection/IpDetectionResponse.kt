package data.network.response.ipDetection

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IpDetectionResponse(
    @SerialName("ip")
    val ip : String,
    @SerialName("country")
    val country : String
)
package data.network.response.availability

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AvailabilityNetworkResponse(
    @SerialName("detail")
    val detail : Boolean,
    @SerialName("obj_id")
    val obj_id : Int? = null
)
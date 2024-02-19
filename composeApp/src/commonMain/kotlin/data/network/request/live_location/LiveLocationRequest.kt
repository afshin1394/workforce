package data.network.request.live_location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveLocationRequest(
    @SerialName("longitude")
    val longitude: Double,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("recorded_date")
    val recorded_date: String,

    @SerialName("site")
    val site: Long
)

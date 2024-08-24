package data.network.request.live_location

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class LiveLocationRequest(
    @SerialName("longitude")
    val longitude: Double,

    @SerialName("latitude")
    val latitude: Double,

    @SerialName("recorded_date")
    val recorded_date: String,

    @SerialName("site")
    val site: Long,

    @SerialName("attendance")
    val attendance: Long,
    @SerialName("ticket_num")
    val ticket_num: String,
    @SerialName("network_info")
    val network_info : JsonObject

)

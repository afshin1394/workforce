package domain.models

import kotlinx.serialization.json.JsonObject

data class LiveLocationDomain(
    val latitude: Double,
    val longitude: Double,
    val recorded_date: String,
    val site: Long,
    val attendance:Long,
    val ticket_num:String,
    val network_info : JsonObject
)

package data.network.request.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class StepRequest(
    @SerialName("activity_id")
    val activity_id : Int,
    @SerialName("values")
    val values : String,
    @SerialName("wi")
    val wi : Int,
    )

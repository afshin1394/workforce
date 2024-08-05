package data.network.request.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitAllRequest(
    @SerialName("steps")
    val steps : ArrayList<StepRequest>,
    @SerialName("ticket_num")
    val ticket_num : String
)
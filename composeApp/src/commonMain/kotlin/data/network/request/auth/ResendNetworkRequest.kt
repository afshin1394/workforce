package data.network.request.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResendNetworkRequest(
    @SerialName("session_id")
    val session_id : String
)

package data.network.response.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("pk")
    val pk : String?,
    @SerialName("username")
    val username : String?,
    @SerialName("email")
    val email : String?
)

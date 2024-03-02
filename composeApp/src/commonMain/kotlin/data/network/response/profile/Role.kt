package data.network.response.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Role(
    @SerialName("code")
    val code : Int?,
    @SerialName("name")
    val name : String?
)

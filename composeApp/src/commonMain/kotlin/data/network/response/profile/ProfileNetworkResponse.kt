package data.network.response.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfileNetworkResponse(
    @SerialName("user")
    val user: User?,
    @SerialName("role")
    val role : List<Role>?,
    @SerialName("first_name")
    val firstName : String?,
    @SerialName("last_name")
    val lastName : String?,
    @SerialName("company")
    val company : String?,
    @SerialName("organization")
    val organization : String?,
    @SerialName("national_id")
    val nationalId : String?,
    @SerialName("phone_number")
    val phoneNumber : String?,
)

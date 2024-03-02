package domain.models

import kotlinx.serialization.SerialName

data class UserDomain(
    val pk : String,
    val username : String,
    val email : String
)

package data.network

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequestNetwork(val username : String, val password : String)
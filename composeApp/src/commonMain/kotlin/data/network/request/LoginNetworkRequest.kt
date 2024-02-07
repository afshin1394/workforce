package data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class LoginNetworkRequest(val username : String, val password : String)
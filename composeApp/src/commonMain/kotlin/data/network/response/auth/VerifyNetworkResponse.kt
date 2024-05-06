package data.network.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class VerifyNetworkResponse(val auth_token : String)


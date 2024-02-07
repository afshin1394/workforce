package data.network.response

import kotlinx.serialization.Serializable

@Serializable
data class VerifyNetworkResponse(val auth_token : String)

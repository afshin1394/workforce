package data.network.response.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginNetworkResponse(val session_id: String, val phone_number: String?)

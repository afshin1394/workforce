package data.network.request

import kotlinx.serialization.Serializable

@Serializable
data class VerifyNetworkRequest(val session_id : String, val code : String  ,val fp : String = "string")
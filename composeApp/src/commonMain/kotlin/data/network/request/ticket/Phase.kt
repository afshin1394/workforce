package data.network.request.ticket

import kotlinx.serialization.Serializable

@Serializable
data class Phase(
    val phase : String,
    val field : String
)

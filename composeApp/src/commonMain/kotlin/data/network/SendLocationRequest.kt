package data.network

import kotlinx.serialization.Serializable

@Serializable
data class SendLocationRequest(
    private val longitude: Double,
    private val latitude: Double
)
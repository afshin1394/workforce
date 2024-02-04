package data.network

import kotlinx.serialization.Serializable

@Serializable
data class ChangeAvailabilityRequest(private val is_ready: Boolean, private val object_id: Int? = null)
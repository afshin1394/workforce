package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Validate(
    val required : Boolean,
)

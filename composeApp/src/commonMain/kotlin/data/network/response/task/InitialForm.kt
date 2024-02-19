package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class InitialForm(
    val id: String? = null,
    val hide: String?= null,
    val type: String?= null,
    val components: List<Component>?= null,
    val conditional : Conditional?= null,
    val schemaVersion : Int?= null,
)

package data.network.response.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitialForm(
    @SerialName("id")
    val id: String? = null,
    @SerialName("hide")
    val hide: String?= null,
    @SerialName("type")
    val type: String?= null,
    @SerialName("components")
    val components: List<Component>?= null,
    @SerialName("conditional")
    val conditional : Conditional?= null,
    @SerialName("schemaVersion")
    val schemaVersion : Int?= null,
)

package data.network.response.task.task

import data.network.response.task.Component
import data.network.response.task.Conditional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitStructure(
    @SerialName("id")
    val id: String? = null,
    @SerialName("hide")
    val hide: String? = null,
    @SerialName("type")
    val type: String? = null,
    @SerialName("mode")
    val mode: String? = null,
    @SerialName("components")
    val components: List<InitComponent>? = null,
    @SerialName("conditional")
    val conditional: Conditional? = null,
    @SerialName("schemaVersion")
    val schemaVersion: Int? = null,
    @SerialName("persistent_keys")
    val persistent_keys: List<String>? = null,
) {
    override fun toString(): String {
        return "FormStruct(id=$id, hide=$hide, type=$type, components=$components, conditional=$conditional, schemaVersion=$schemaVersion)"
    }
}

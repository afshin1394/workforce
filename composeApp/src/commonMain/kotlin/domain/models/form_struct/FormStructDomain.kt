package domain.models.form_struct

import kotlinx.serialization.Serializable

@Serializable
data class FormStructDomain (
    val id: String? = null,
    val hide: String?= null,
    val type: String?= null,
    var components: List<ComponentDomain>?= null,
    val conditional : ConditionalDomain?= null,
    val schemaVersion : Int?= null,
) {
    override fun toString(): String {
        return "FormStructDomain(id=$id, hide=$hide, type=$type, components=$components, conditional=$conditional, schemaVersion=$schemaVersion)"
    }
}
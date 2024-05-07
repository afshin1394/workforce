package domain.models.initialForm

import data.network.response.task.Component
import data.network.response.task.Conditional
import kotlinx.serialization.Serializable

@Serializable
data class InitialFormStructureDomain (
    val id: String? = null,
    val hide: String?= null,
    val type: String?= null,
    val components: ArrayList<ComponentDomain>?= null,
    val conditional : ConditionalDomain?= null,
    val schemaVersion : Int?= null,
){
    override fun toString(): String {
        return "InitialFormStructureDomain(id=$id, hide=$hide, type=$type, components=$components, conditional=$conditional, schemaVersion=$schemaVersion)"
    }
}
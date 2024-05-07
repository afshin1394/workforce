package domain.models.initialForm

import kotlinx.serialization.Serializable


@Serializable
data class ComponentDomain(
    val id: String?= null,
    val hide: String?= null,
    val type: String?= null,
    val label: String?= null,
    val layout: LayoutDomain?= null,
    val subType : String?= null,
    val validate : ValidateDomain?= null,
    var values: List<ValueDomain>?= null,
    val conditional : ConditionalDomain?= null,
    val components : ArrayList<ComponentDomain>?= null,
    val logics : List<LogicDomain>?= null
){
    override fun toString(): String {
        return "ComponentDomain(id=$id, hide=$hide, type=$type, label=$label, layout=$layout, subType=$subType, validate=$validate, values=$values, conditional=$conditional, components=$components, logics=$logics)"
    }
}

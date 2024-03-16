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
    val values: List<ValueDomain>?= null,
    val conditional : ConditionalDomain?= null,
    val components : List<ComponentDomain>?= null,
    val logics : List<LogicDomain>?= null
)

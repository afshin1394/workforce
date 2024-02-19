package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Component(
    val id: String?= null,
    val hide: String?= null,
    val type: String?= null,
    val label: String?= null,
    val layout: Layout?= null,
    val subType : String?= null,
    val validate : Validate?= null,
    val values: List<Value>?= null,
    val conditional : Conditional?= null,
    val components : List<Component>?= null,
    val logics : List<Logic>?= null
)

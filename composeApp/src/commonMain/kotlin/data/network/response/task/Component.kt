package data.network.response.task

import data.network.response.task.logic.Logic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Component(
    @SerialName("id")
    val id: String? = null,
    @SerialName("key")
    val key: String? = null,
    @SerialName("hide")
    val hide: String? = null,
    @SerialName("type")
    val type: String = "default",
    @SerialName("label")
    val label: String? = null,
    @SerialName("layout")
    val layout: Layout? = null,
    @SerialName("subtype")
    val subType: String? = null,
    @SerialName("isMulti")
    val isMulti: Boolean? = null,
    @SerialName("validate")
    val validate: Validate? = null,
    @SerialName("values")
    val values: List<Value>? = null,
    @SerialName("conditional")
    val conditional: Conditional? = null,
    @SerialName("components")
    val components: List<Component>? = null,
    @SerialName("readonly")
    val readOnly: Boolean? = null,
    @SerialName("repeatable")
    val repeatable: Boolean? = null,
    @SerialName("removable")
    val removable: Boolean? = null,
    @SerialName("disabled")
    val disabled: Boolean? = null,
    @SerialName("defaultValue")
    val defaultValue: String? = null,
    @SerialName("logics")
    val logics: List<Logic>? = null
)

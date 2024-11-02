package data.network.response.task

import data.network.response.task.logic.Logic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.AnySerializer

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
    @Serializable(with = AnySerializer::class) val defaultValue: Any? = null,
    @SerialName("logics")
    val logics: List<Logic>? = null
){
    override fun toString(): String {
        return "Component(id=$id, key=$key, hide=$hide, type='$type', label=$label, layout=$layout, subType=$subType, isMulti=$isMulti, validate=$validate, values=$values, conditional=$conditional, components=$components, readOnly=$readOnly, repeatable=$repeatable, removable=$removable, disabled=$disabled, defaultValue=$defaultValue, logics=$logics)"
    }
}

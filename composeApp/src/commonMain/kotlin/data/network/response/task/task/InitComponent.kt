package data.network.response.task.task

import data.network.response.task.Conditional
import data.network.response.task.Layout
import data.network.response.task.Validate
import data.network.response.task.Value
import data.network.response.task.logic.Logic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.AnySerializer

@Serializable
data class InitComponent(
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
    @SerialName("subtype")
    val subType: String? = null,
    @SerialName("values")
    val values: List<Value>? = null,
    @SerialName("isMulti")
    val isMulti: Boolean? = null,
    @SerialName("conditional")
    val conditional: Conditional? = null,
    @SerialName("components")
    val components: List<InitComponent>? = null,
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
    val logics: List<InitLogic>? = null,
    @SerialName("injected_value")
    @Serializable(with = AnySerializer::class)
    val injected_value: Any? = null

){
    override fun toString(): String {
        return "Component(id=$id, key=$key, hide=$hide, type='$type', label=$label,  subType=$subType, isMulti=$isMulti, conditional=$conditional, components=$components, readOnly=$readOnly, repeatable=$repeatable, removable=$removable, disabled=$disabled, defaultValue=$defaultValue,injected_value=$injected_value)"
    }
}

package domain.models.form_struct

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import data.network.response.task.logic.LogicDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json
import presentation.model.ExtractLogicsModel
import utils.AnySerializer


@Serializable
data class ComponentDomain(
    val id: String? = null,
    val key: String? = null,
    val hide: String? = null,
    val type: String? = null,
    val label: String? = null,
    val layout: LayoutDomain? = null,
    val subType: String? = null,
    var validate: ValidateDomain? = null,
    var values: List<ValueDomain>? = null,
    val conditional: ConditionalDomain? = null,
    var _components: List<ComponentDomain>? = null,
    val logics: List<LogicDomain>? = null,
    val repeatable: Boolean = false,
    val removable: Boolean? = null,
    val isMulti: Boolean = false,
    val readOnly: Boolean = false,
    val disabled: Boolean = false,
    @Serializable(with = AnySerializer::class)
    val defaultValue: Any? = null,


    //in app properties
    var _processLogicDomain: ProcessLogicDomain = ProcessLogicDomain().copy()


) {
    // Use MutableState for UI components
    var components: MutableState<List<ComponentDomain>?> = mutableStateOf(_components)
    var processLogicDomain: MutableState<ProcessLogicDomain> = mutableStateOf(_processLogicDomain)
    var valuesState: MutableState<List<ValueDomain>?> = mutableStateOf(values)

    // Method to create a new instance with updated components
    fun updateComponents(newComponents: List<ComponentDomain>): ComponentDomain {
        components.value = newComponents // Update the MutableState
        _components = newComponents
        return this.copy(_components = newComponents) // Return a new instance for serialization
    }

    fun updateProcessLogicDomain(newProcessLogicDomain: ProcessLogicDomain): ComponentDomain {
        this.processLogicDomain.value = newProcessLogicDomain // Update the MutableState
        _processLogicDomain = newProcessLogicDomain
        return this.copy(_processLogicDomain = newProcessLogicDomain) // Return a new instance for serialization
    }

    fun updateValues(newValues: List<ValueDomain>?): ComponentDomain {
        valuesState.value = newValues // Update the MutableState
        values = newValues
        return this.copy(values = newValues) // Return a new instance for serialization
    }

    override fun toString(): String {
        return "ComponentDomain(id=$id, key=$key, hide=$hide, type=$type, label=$label, layout=$layout, subType=$subType, validate=$validate, values=$values, conditional=$conditional, _components=$_components, logics=$logics, repeatable=$repeatable, removable=$removable, isMulti=$isMulti, readOnly=$readOnly, disabled=$disabled, defaultValue=$defaultValue, _processLogicDomain=$_processLogicDomain, components=$components, processLogicDomain=$processLogicDomain, valuesState=$valuesState)"
    }


}



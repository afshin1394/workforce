package domain.models.form_struct

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import data.network.response.task.logic.LogicDomain
import domain.models.form_struct.logic.InitLogicDomain
import kotlinx.serialization.Serializable
import utils.AnySerializer
import utils.FormViewerTypes
import utils.localDateTimeToMilliseconds
import utils.parseLocalDateTime
@Serializable
data class InitComponentDomain(
    val id: String? = null,
    val key: String? = null,
    val hide: String? = null,
    val type: String? = null,
    val label: String? = null,
    val subType: String? = null,
    val conditional: ConditionalDomain? = null,
    var _components: List<InitComponentDomain>? = null,
    val logics: List<InitLogicDomain>? = null,
    val repeatable: Boolean = false,
    val removable: Boolean? = null,
    var values: List<ValueDomain>? = null,
    val isMulti: Boolean = false,
    val readOnly: Boolean = false,
    val disabled: Boolean = false,
    @Serializable(with = AnySerializer::class)
    val defaultValue: Any? = null,
    @Serializable(with = AnySerializer::class)
    val injected_value: Any? = null,
    //in app properties
    var _processLogicDomain: InitProcessLogicDomain = InitProcessLogicDomain().copy()
) {
    // Use MutableState for UI components
    var components: MutableState<List<InitComponentDomain>?> = mutableStateOf(_components)
    var processLogicDomain: MutableState<InitProcessLogicDomain> = mutableStateOf(_processLogicDomain)
    var valuesState: MutableState<List<ValueDomain>?> = mutableStateOf(values)



    fun updateProcessLogicDomain(newProcessLogicDomain: InitProcessLogicDomain): InitComponentDomain {
        this.processLogicDomain.value = newProcessLogicDomain // Update the MutableState
        _processLogicDomain = newProcessLogicDomain
        return this.copy(_processLogicDomain = newProcessLogicDomain) // Return a new instance for serialization
    }
    fun updateValues(newValues: List<ValueDomain>?): InitComponentDomain {
        valuesState.value = newValues // Update the MutableState
        values = newValues
        return this.copy(values = newValues) // Return a new instance for serialization
    }

    override fun toString(): String {
        return "ComponentDomain(id=$id, key=$key, hide=$hide, type=$type, label=$label, subType=$subType, values=$injected_value, conditional=$conditional, _components=$_components, logics=$logics, repeatable=$repeatable, removable=$removable, isMulti=$isMulti, readOnly=$readOnly, disabled=$disabled, defaultValue=$defaultValue, _processLogicDomain=$_processLogicDomain, processLogicDomain=$processLogicDomain)"
    }
    fun getValueBaseOnType() : Double? {
        return  when(this.type){
            FormViewerTypes.Datetime ->{
                this.values?.get(0)?.value?.parseLocalDateTime()?.localDateTimeToMilliseconds()?.toString()?.toDoubleOrNull()
            }
            else->{
                this.values?.get(0)?.value?.toDoubleOrNull()
            }
        }

    }

}



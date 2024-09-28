package domain.models.form_struct

import androidx.compose.runtime.saveable.Saver
import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import data.network.response.task.logic.LogicDomain
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.Json

@Serializable
data class ComponentDomain(
    val id: String?= null,
    val key: String?=null,
    val hide: String?= null,
    val type: String?= null,
    val label: String?= null,
    val layout: LayoutDomain?= null,
    val subType : String?= null,
    var validate : ValidateDomain?= null,
    var values: List<ValueDomain>?= null,
    val conditional : ConditionalDomain?= null,
    var components : List<ComponentDomain>?= null,
    val logics : List<LogicDomain>?= null,
    val repeatable:Boolean=false,
    val removable:Boolean?=null,
    val isMulti:Boolean=false,
    val readOnly : Boolean = false,
    val disabled : Boolean = false,

    //in app properties
    var processLogicDomain : ProcessLogicDomain=ProcessLogicDomain().copy()


    )  {


    fun ComponentDomain.copy() : ComponentDomain{
              return ComponentDomain(this.id)
    }

    override fun toString(): String {
        return "ComponentDomain(id=$id, key=$key, hide=$hide, type=$type, label=$label, layout=$layout, subType=$subType, validate=$validate, values=$values, conditional=$conditional, components=$components, logics=$logics, repeatable=$repeatable, removable=$removable, isMulti=$isMulti, readOnly=$readOnly, processLogicDomain=$processLogicDomain)"
    }

    companion object {
        // Custom Saver for ComponentDomain
        val componentDomainSaver = Saver<ComponentDomain, String>(
            save = { componentDomain ->
                Json.encodeToString(serializer(), componentDomain)
            },
            restore = { jsonString ->
                Json.decodeFromString<ComponentDomain>(jsonString)
            }
        )
    }
}



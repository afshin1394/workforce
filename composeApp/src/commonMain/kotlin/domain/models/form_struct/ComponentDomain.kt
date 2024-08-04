package domain.models.form_struct

import data.network.response.task.logic.LogicDomain


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
    val removable:Boolean=false,
    val isMulti:Boolean=false,
    val readOnly : Boolean = false,

    //in app properties
    var processLogicDomain : ProcessLogicDomain=ProcessLogicDomain().copy(),


    ){


    fun ComponentDomain.copy() : ComponentDomain{
              return ComponentDomain(this.id)
    }

    override fun toString(): String {
        return "ComponentDomain(id=$id, key=$key, hide=$hide, type=$type, label=$label, layout=$layout, subType=$subType, validate=$validate, values=$values, conditional=$conditional, components=$components, logics=$logics, repeatable=$repeatable, removable=$removable, isMulti=$isMulti, readOnly=$readOnly, processLogicDomain=$processLogicDomain)"
    }


}

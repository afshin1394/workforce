package domain.mappers

import data.network.response.task.Component
import data.network.response.task.Condition
import data.network.response.task.Conditional
import data.network.response.task.Expression
import data.network.response.task.InitialForm
import data.network.response.task.Layout
import data.network.response.task.Logic
import data.network.response.task.Operator
import data.network.response.task.TasksNetworkResponse
import data.network.response.task.Validate
import data.network.response.task.Value
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ConditionDomain
import domain.models.initialForm.ConditionalDomain
import domain.models.initialForm.ExpressionDomain
import domain.models.initialForm.InitialFormDomain
import domain.models.initialForm.InitialFormStructureDomain
import domain.models.initialForm.LayoutDomain
import domain.models.initialForm.LogicDomain
import domain.models.initialForm.OperatorDomain
import domain.models.initialForm.ValidateDomain
import domain.models.initialForm.ValueDate
import domain.models.initialForm.ValueDomain
import irancell.nwg.wfm.db.InitialFormEntity
import kotlinx.serialization.json.Json

fun TasksNetworkResponse.toInitialFormEntity(): InitialFormEntity {
    return   InitialFormEntity(
        wi_id = this.wi_id,
        structure = this.initial_form.toString()
    )
}

fun List<TasksNetworkResponse>.toInitialFormEntity(): List<InitialFormEntity> {
    return  map{
        InitialFormEntity(
            wi_id = it.wi_id,
            structure = it.initial_form.toString()
        )
    }
}

fun InitialFormEntity.toInitialFormDomain() : InitialFormDomain {
      val  initialForm = Json.decodeFromString<InitialForm>(this.structure);


    return initialForm.toInitialFormDomain(this.wi_id)
}

fun  List<InitialFormEntity>.toInitialFormDomain() : List<InitialFormDomain> {
    return map {
        InitialFormDomain(
            wi_id = it.wi_id,
            structure = Json.decodeFromString(it.structure)
        )
    }
}
fun  InitialForm.toInitialFormDomain(wi_id : Long) : InitialFormDomain {
    return InitialFormDomain(wi_id , InitialFormStructureDomain(this.id,this.hide,this.type,this.components?.toComponentDomain() ,this.conditional?.toConditionalDomain(),this.schemaVersion)     )
}

 fun  List<Component>.toComponentDomain() : List<ComponentDomain> {
    return map {  ComponentDomain(it.id,it.hide,it.type,it.label,it.layout?.toLayoutDomain(),it.subType,it.validate?.toValidateDomain(),it.values?.toValueDomain(),it.conditional?.toConditionalDomain(),it.components?.toComponentDomain(),it.logics?.toLogicDomain())   }
}
 fun Layout.toLayoutDomain():LayoutDomain{
     return LayoutDomain(this.row,this.columns)
 }

fun Validate.toValidateDomain():ValidateDomain{
    return ValidateDomain(this.id,this.key,this.id,this.layout,this.subtype,this.required)

}

fun List<Value> .toValueDomain():List<ValueDomain>{
     return map{
         ValueDomain(it.label,it.value.toString(), ValueDate("",""),false)
     }
}

fun Conditional.toConditionalDomain(): ConditionalDomain{
    return ConditionalDomain(this.string)
}

 fun List<Logic>.toLogicDomain():List<LogicDomain>{
     return map{
         LogicDomain(it.logicType,it.experssions?.toExpressionDomain())
     }
 }

fun List<Expression>.toExpressionDomain():List<ExpressionDomain>{
    return map{
        ExpressionDomain(it.conditions?.toConditionDomain())
    }
}
 fun List<Condition>.toConditionDomain():List<ConditionDomain>{
     return map{
         ConditionDomain(it.firstFieldKey,it.secondOperator?.toOperatorDomain(),it.value)
     }
 }

fun Operator.toOperatorDomain():OperatorDomain{
    return OperatorDomain(this.title,this.symbol)
}





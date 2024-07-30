package domain.mappers

import data.network.response.task.Component
import data.network.response.task.Condition
import data.network.response.task.Conditional
import data.network.response.task.Expression
import data.network.response.task.FormStruct
import data.network.response.task.Layout
import data.network.response.task.Operator
import data.network.response.task.Validate
import data.network.response.task.Value
import data.network.response.task.logic.AutoFillLogic
import data.network.response.task.logic.AutoFillLogicDomain
import data.network.response.task.logic.BindLogic
import data.network.response.task.logic.BindLogicDomian
import data.network.response.task.logic.FieldOption
import data.network.response.task.logic.FieldOptionDomain
import data.network.response.task.logic.FilterOptionsLogic
import data.network.response.task.logic.FilterOptionsLogicDomain
import data.network.response.task.logic.Logic
import data.network.response.task.logic.LogicCondition
import data.network.response.task.logic.LogicConditionDomain
import data.network.response.task.logic.LogicDomain
import data.network.response.task.logic.TicketAutoFillLogic
import data.network.response.task.logic.TicketAutoFillLogicDomain
import data.network.response.task.task.Detail
import database.entity.InitialFormEntity
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.logic.ConditionDomain
import domain.models.form_struct.ConditionalDomain
import domain.models.form_struct.logic.ExpressionDomain
import domain.models.form_struct.InitialFormDomain
import domain.models.form_struct.FormStructDomain
import domain.models.form_struct.LayoutDomain
import domain.models.form_struct.OperatorDomain
import domain.models.form_struct.ValidateDomain
import domain.models.form_struct.ValueDate
import domain.models.form_struct.ValueDomain

import kotlinx.serialization.json.Json

fun Detail.toInitialFormEntity(): InitialFormEntity {
    return   InitialFormEntity(
        ticket_number = this.basic_info.ticket_number?:"",
        structure = this.initial_form.toString()
    )
}

fun List<Detail>.toInitialFormEntity(): List<InitialFormEntity> {
    return  map{
        InitialFormEntity(
            ticket_number = it.basic_info.ticket_number?:"",
            structure = it.initial_form.toString()
        )
    }
}

fun InitialFormEntity.toInitialFormDomain() : InitialFormDomain {
      val  formStruct = Json.decodeFromString<FormStruct>(this.structure);


    return formStruct.toInitialFormDomain(this.ticket_number)
}

fun  List<InitialFormEntity>.toInitialFormDomain() : List<InitialFormDomain> {
    return map {
        InitialFormDomain(
            ticket_number = it.ticket_number,
            structure = Json.decodeFromString(it.structure)
        )
    }
}

fun  FormStruct.toInitialFormDomain(ticketNumber : String) : InitialFormDomain {
    return InitialFormDomain(ticketNumber , FormStructDomain(this.id,this.hide,this.type,this.components?.toComponentDomain() ,this.conditional?.toConditionalDomain(),this.schemaVersion)     )
}

 fun  List<Component>.toComponentDomain() : List<ComponentDomain> {
    return map {  ComponentDomain(it.id,it.hide,it.type,it.label,it.layout?.toLayoutDomain(),it.subType,it.validate?.toValidateDomain(),it.values?.toValueDomain(),it.conditional?.toConditionalDomain(),it.components?.toComponentDomain())   }
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
         LogicDomain(it.feild,it.logicType,it.experssions?.toExpressionDomain(),it.filterOptionsLogic?.toFilterOptionsDomain(), it.autoFillLogic?.toAutoFillLogicDomain(),it.bind_logic?.toBindLogicDomain(),it.ticketAutoFillLogic?.toTicketAutoFillLogicDomain())
     }
 }

fun BindLogic.toBindLogicDomain() : BindLogicDomian{
  return  BindLogicDomian(this.field_options.toBindLogicOptionsDomain())
}

fun List<FieldOption>.toBindLogicOptionsDomain() : List<FieldOptionDomain>{
    return map{
        FieldOptionDomain(it.field_key)
    }
}

fun List<FilterOptionsLogic>.toFilterOptionsDomain() : List<FilterOptionsLogicDomain>{
    return map{
        FilterOptionsLogicDomain(it.conditions?.toLogicConditionDomain(),it.filteredOptions,it.multiSelectValues,it.selectedKeyValues)
    }
}
fun List<LogicCondition>.toLogicConditionDomain() : List<LogicConditionDomain>{
  return  map{
        LogicConditionDomain(it.title,it.firstField,it.secondField,it.secondFieldKey,it.firstOperator,it.secondOperator,it.value,it.values,it.multiSelectValues,it.selectedKeyValues,it.filteredOptions,it.filterParameter,it.apiFilterOptionValue)
    }
}

fun TicketAutoFillLogic.toTicketAutoFillLogicDomain() : TicketAutoFillLogicDomain{
    return TicketAutoFillLogicDomain(this.phaseName,this.phase,this.property,this.condition_key,this.condition_field,this.condition_value)
}
fun AutoFillLogic.toAutoFillLogicDomain() :  AutoFillLogicDomain{
   return AutoFillLogicDomain(this.api,this.apiName,this.filterField,this.filterFieldKey,this.filterParameter,this.property)
}
fun List<Expression>.toExpressionDomain():List<ExpressionDomain>{
    return map{
        ExpressionDomain(it.conditions?.toConditionDomain())
    }
}
 fun List<Condition>.toConditionDomain():List<ConditionDomain>{
     return map{
         ConditionDomain(it.firstFieldKey,it.secondFieldKey,it.firstOperator?.toOperatorDomain(),it.secondOperator?.toOperatorDomain(),it.value)
     }
 }

fun Operator.toOperatorDomain():OperatorDomain{
    return OperatorDomain(this.title,this.symbol)
}


fun  List<ComponentDomain>.toComponent() : List<Component> {
    return map {  Component(it.id,it.hide,it.type,it.label,it.layout?.toLayout(),it.subType,it.validate?.toValidate(),it.values?.toValue(),it.conditional?.toConditional(),it.components?.toComponent())   }
}




fun LayoutDomain.toLayout():Layout{
    return Layout(this.row,this.columns)
}

fun ValidateDomain.toValidate():Validate{
    return Validate(this.id,this.key,this.id,this.layout,this.subtype,this.required)

}

fun List<ValueDomain> .toValue():List<Value>{
    return map{
        Value(it.label,it.value.toString())
    }
}

fun ConditionalDomain.toConditional(): Conditional{
    return Conditional(this.string)
}

fun List<LogicDomain>.toLogic():List<Logic>{
    return map{
        Logic(it.feild,it.logicType,it.experssions?.toExpression(),it.filterOptionsLogic?.toFilterOptions(), it.autoFillLogicDomain?.toAutoFillLogic(),it.bind_logic?.toBindLogic(),it.ticketAutoFillLogicDomain?.toTicketAutoFillLogic())
    }
}

fun BindLogicDomian.toBindLogic() : BindLogic{
    return  BindLogic(this.field_options.toBindLogicOptions())
}

fun List<FieldOptionDomain>.toBindLogicOptions() : List<FieldOption>{
    return map{
        FieldOption(it.field_key)
    }
}

fun List<FilterOptionsLogicDomain>.toFilterOptions() : List<FilterOptionsLogic>{
    return map{
        FilterOptionsLogic(it.conditions?.toLogicCondition(),it.filteredOptions,it.multiSelectValues,it.selectedKeyValues)
    }
}
fun List<LogicConditionDomain>.toLogicCondition() : List<LogicCondition>{
    return  map{
        LogicCondition(it.title,it.firstField,it.secondField,it.secondFieldKey,it.firstOperator,it.secondOperator,it.value,it.values,it.multiSelectValues,it.selectedKeyValues,it.filteredOptions,it.filterParameter,it.apiFilterOptionValue)
    }
}

fun TicketAutoFillLogicDomain.toTicketAutoFillLogic() : TicketAutoFillLogic{
    return TicketAutoFillLogic(this.phaseName,this.phase,this.property,this.condition_key,this.condition_field,this.condition_value)
}
fun AutoFillLogicDomain.toAutoFillLogic() :  AutoFillLogic{
    return AutoFillLogic(this.api,this.apiName,this.filterField,this.filterFieldKey,this.filterParameter,this.property)
}
fun List<ExpressionDomain>.toExpression():List<Expression>{
    return map{
        Expression(it.conditions?.toCondition())
    }
}
fun List<ConditionDomain>.toCondition():List<Condition>{
    return map{
        Condition(it.firstFieldKey,it.secondFieldKey,it.firstOperator?.toOperator(),it.secondOperator?.toOperator(),it.value)
    }
}

fun OperatorDomain.toOperator():Operator{
    return Operator(this.title,this.symbol?:"")
}




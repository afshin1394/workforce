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
import data.network.response.task.logic.TicketAutoFillOption
import data.network.response.task.task.Detail
import data.network.response.task.task.InitForm
import database.entity.InitialFormEntity
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.logic.ConditionDomain
import domain.models.form_struct.ConditionalDomain
import domain.models.form_struct.logic.ExpressionDomain
import domain.models.form_struct.InitialFormDomain
import domain.models.form_struct.LayoutDomain
import domain.models.form_struct.OperatorDomain
import domain.models.form_struct.ValidateDomain
import domain.models.form_struct.ValueDate
import domain.models.form_struct.ValueDomain
import domain.models.form_struct.logic.TicketAutoFillOptionDomain
import domain.models.task.InitFormDomain


private fun mapSubtypeToType(subtype: String?): String? {
    return when (subtype) {
        "datetime" -> "datetime"
        "date" -> "date"
        "time" -> "time"
        "multi" -> "multi"
        else -> null
    }

}

private fun mapTypeToSubType(type: String?): String? {
    return when (type) {
        "datetime" -> "datetime"
        "date" -> "datetime"
        "time" -> "datetime"
        "multi" -> "select"
        else -> null
    }

}

private fun List<ComponentDomain>.updateComponentTypes(): List<ComponentDomain> {
    return this.map { component ->
        val newType = mapSubtypeToType(component.subType) ?: component.type
        component.copy(
            type = newType,
            components = component.components?.updateComponentTypes()
        )
    }
}

private fun List<Component>.updateComponentTypesReverse(): List<Component> {
    return this.map { component ->
        val newType = mapTypeToSubType(component.type) ?: component.type
        component.copy(
            type = newType,
            components = component.components?.updateComponentTypesReverse()
        )
    }
}


fun InitialFormEntity.toInitialFormDomain(): InitialFormDomain {
    return InitialFormDomain(this.ticket_number, this.initFormList.toInitFormDomainList())
}

fun List<InitialFormEntity>.toInitialFormDomainList(): List<InitialFormDomain> {
    return map {
        it.toInitialFormDomain()
    }
}


fun List<InitForm>.toInitFormDomainList(): List<InitFormDomain> {
    return map {
        InitFormDomain(it.key, it.value)
    }
}

fun List<Component>.toComponentDomain(): List<ComponentDomain> {
    return map {
        ComponentDomain(
            id = it.id,
            it.key,
            hide = it.hide,
            type = it.type,
            label = it.label,
            readOnly = it.readOnly ?: false,
            repeatable = it.repeatable ?: false,
            removable = it.removable ?: false,
            layout = it.layout?.toLayoutDomain(),
            subType = it.subType,
            isMulti = it.isMulti ?: false,
            validate = it.validate?.toValidateDomain(),
            values = it.values?.toValueDomain(),
            conditional = it.conditional?.toConditionalDomain(),
            components = it.components?.toComponentDomain(),
            logics = it.logics?.toLogicDomain()
        )
    }.updateComponentTypes()
}


fun Layout.toLayoutDomain(): LayoutDomain {
    return LayoutDomain(this.row, this.columns)
}

fun Validate.toValidateDomain(): ValidateDomain? {
    return if (this.areAllMembersNull()) {
        null
    } else {
        ValidateDomain(
            this.id,
            this.key,
            this.id,
            this.layout,
            this.subtype,
            this.required
        )
    }


}

fun List<Value>.toValueDomain(): List<ValueDomain> {
    return map {
        ValueDomain(it.label, it.value.toString(), ValueDate("", ""), it.isSelected)
    }
}

fun Conditional.toConditionalDomain(): ConditionalDomain {
    return ConditionalDomain(this.string)
}

fun List<Logic>.toLogicDomain(): List<LogicDomain> {
    return map {
        LogicDomain(
            it.logicType,
            it.experssions?.toExpressionDomain(),
            it.filterOptionsLogic?.toFilterOptionsDomain(),
            it.autoFillLogic?.toAutoFillLogicDomain(),
            it.bind_logic?.toBindLogicDomain(),
            it.ticketAutoFillLogic?.toTicketAutoFillLogicDomain()
        )
    }
}

fun BindLogic.toBindLogicDomain(): BindLogicDomian {
    return BindLogicDomian(this.field_options.toBindLogicOptionsDomain())
}

fun List<FieldOption>.toBindLogicOptionsDomain(): List<FieldOptionDomain> {
    return map {
        FieldOptionDomain(it.field_key)
    }
}

fun List<FilterOptionsLogic>.toFilterOptionsDomain(): List<FilterOptionsLogicDomain> {
    return map {
        FilterOptionsLogicDomain(
            it.conditions?.toLogicConditionDomain(),
            it.filteredOptions,
            it.multiSelectValues,
            it.selectedKeyValues
        )
    }
}

fun List<LogicCondition>.toLogicConditionDomain(): List<LogicConditionDomain> {
    return map {
        LogicConditionDomain(
            it.title,
            it.firstField,
            it.secondField,
            it.secondFieldKey,
            it.firstOperator,
            it.secondOperator,
            it.value,
            it.values,
            it.multiSelectValues,
            it.selectedKeyValues,
            it.filteredOptions,
            it.filterParameter,
            it.apiFilterOptionValue
        )
    }
}

fun TicketAutoFillLogic.toTicketAutoFillLogicDomain(): TicketAutoFillLogicDomain {
    return TicketAutoFillLogicDomain(this.options.toTicketAutoFillOptionDomain())
}

fun List<TicketAutoFillOption>.toTicketAutoFillOptionDomain(): List<TicketAutoFillOptionDomain> {
    return map {
        TicketAutoFillOptionDomain(
            it.phaseName,
            it.property,
            it.condition_key,
            it.condition_value
        )
    }
}

fun AutoFillLogic.toAutoFillLogicDomain(): AutoFillLogicDomain {
    return AutoFillLogicDomain(
        this.api,
        this.apiName,
        this.filterField,
        this.filterFieldKey,
        this.filterParameter,
        this.property
    )
}

fun List<Expression>.toExpressionDomain(): List<ExpressionDomain> {
    return map {
        ExpressionDomain(it.conditions?.toConditionDomain())
    }
}

fun List<Condition>.toConditionDomain(): List<ConditionDomain> {
    return map {
        ConditionDomain(
            it.firstFieldKey,
            it.secondFieldKey,
            it.firstOperator?.toOperatorDomain(),
            it.secondOperator?.toOperatorDomain(),
            value = it.value,
            values = it.values
        )
    }
}

fun Operator.toOperatorDomain(): OperatorDomain {
    return OperatorDomain(this.title, this.symbol)
}


fun List<ComponentDomain>.toComponent(): List<Component> {
    return map {
        Component(
            id = it.id,
            it.key,
            hide = it.hide,
            type = it.type ?: "default",
            repeatable = it.repeatable,
            removable = it.removable,
            readOnly = it.readOnly,
            label = it.label,
            layout = it.layout?.toLayout(),
            subType = it.subType,
            isMulti = it.isMulti,
            validate = it.validate?.toValidate(),
            values = it.values?.toValue(),
            conditional = it.conditional?.toConditional(),
            components = it.components?.toComponent(),
            logics = it.logics?.toLogic()
        )
    }.updateComponentTypesReverse()
}


fun LayoutDomain.toLayout(): Layout {
    return Layout(this.row, this.columns)
}

fun ValidateDomain.toValidate(): Validate {
    return Validate(this.id, this.key, this.id, this.layout, this.subtype, this.required)

}

fun List<ValueDomain>.toValue(): List<Value> {
    return map {
        Value(it.label, it.value.toString(), it.isSelected)
    }
}

fun ConditionalDomain.toConditional(): Conditional {
    return Conditional(this.string)
}

fun List<LogicDomain>.toLogic(): List<Logic> {
    return map {
        Logic(
            it.logicType,
            it.experssions?.toExpression(),
            it.filterOptionsLogic?.toFilterOptions(),
            it.autoFillLogicDomain?.toAutoFillLogic(),
            it.bind_logic?.toBindLogic(),
            it.ticketAutoFillLogicDomain?.toTicketAutoFillLogic()
        )
    }
}

fun BindLogicDomian.toBindLogic(): BindLogic {
    return BindLogic(this.field_options.toBindLogicOptions())
}

fun List<FieldOptionDomain>.toBindLogicOptions(): List<FieldOption> {
    return map {
        FieldOption(it.field_key)
    }
}

fun List<FilterOptionsLogicDomain>.toFilterOptions(): List<FilterOptionsLogic> {
    return map {
        FilterOptionsLogic(
            it.conditions?.toLogicCondition(),
            it.filteredOptions,
            it.multiSelectValues,
            it.selectedKeyValues
        )
    }
}

fun List<LogicConditionDomain>.toLogicCondition(): List<LogicCondition> {
    return map {
        LogicCondition(
            it.title,
            it.firstField,
            it.secondField,
            it.secondFieldKey,
            it.firstOperator,
            it.secondOperator,
            it.value,
            it.values,
            it.multiSelectValues,
            it.selectedKeyValues,
            it.filteredOptions,
            it.filterParameter,
            it.apiFilterOptionValue
        )
    }
}

fun TicketAutoFillLogicDomain.toTicketAutoFillLogic(): TicketAutoFillLogic {
    return TicketAutoFillLogic(this.options.toTicketAutoFillOption())
}


fun List<TicketAutoFillOptionDomain>.toTicketAutoFillOption(): List<TicketAutoFillOption> {
    return map {
        TicketAutoFillOption(
            phaseName = it.phaseName,
            property = it.property,
            condition_key = it.condition_key,
            condition_value = it.condition_value
        )
    }
}

fun AutoFillLogicDomain.toAutoFillLogic(): AutoFillLogic {
    return AutoFillLogic(
        this.api,
        this.apiName,
        this.filterField,
        this.filterFieldKey,
        this.filterParameter,
        this.property
    )
}

fun List<ExpressionDomain>.toExpression(): List<Expression> {
    return map {
        Expression(it.conditions?.toCondition())
    }
}

fun List<ConditionDomain>.toCondition(): List<Condition> {
    return map {
        Condition(
            firstFieldKey = it.firstFieldKey,
            secondFieldKey = it.secondFieldKey,
            firstOperator = it.firstOperator?.toOperator(),
            secondOperator = it.secondOperator?.toOperator(),
            values = it.values,
            value = it.value
        )
    }
}

fun OperatorDomain.toOperator(): Operator {
    return Operator(this.title, this.symbol ?: "")
}




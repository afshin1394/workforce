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
import data.network.response.task.step.Form
import data.network.response.task.task.InitComponent
import data.network.response.task.task.InitCondition
import data.network.response.task.task.InitExpression
import data.network.response.task.task.InitForm
import data.network.response.task.task.InitLogic
import data.network.response.task.task.InitOperator
import data.network.response.task.task.InitStructure
import data.network.response.task.task.InstanceTicketsBasicInformationValues
import database.entity.InitialFormEntity
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.logic.ConditionDomain
import domain.models.form_struct.ConditionalDomain
import domain.models.form_struct.FormStructDomain
import domain.models.form_struct.InitComponentDomain
import domain.models.form_struct.InitFormStructDomain
import domain.models.form_struct.logic.ExpressionDomain
import domain.models.form_struct.InitialFormDomain
import domain.models.form_struct.LayoutDomain
import domain.models.form_struct.OperatorDomain
import domain.models.form_struct.ValidateDomain
import domain.models.form_struct.ValueDate
import domain.models.form_struct.ValueDomain
import domain.models.form_struct.logic.InitLogicDomain
import domain.models.form_struct.logic.TicketAutoFillOptionDomain
import domain.models.steps.FormDomain
import domain.models.task.InstanceTicketsBasicInformationValuesDomain
import kotlinx.serialization.json.Json

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
            _components = component.components.value?.updateComponentTypes()
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
    val json = Json { ignoreUnknownKeys = true }

    val initStruct = json.decodeFromString(InitStructure.serializer(), this.initFormJson)

    return InitialFormDomain(
        ticket_number = this.ticket_number,
        initForms = this.initForms.toInitFormDomainList(),
        initStructure = initStruct.toInitFormStructDomain()
    )
}


fun InitStructure.toInitFormStructDomain() = InitFormStructDomain(
    this.id,
    this.hide,
    this.type,
    this.components?.toInitComponentDomain(),
    this.conditional?.toConditionalDomain(),
    this.schemaVersion
)

fun List<InitComponent>.toInitComponentDomain(): List<InitComponentDomain> {
    return map {
        InitComponentDomain(
            id = it.id,
            it.key,
            hide = it.hide,
            type = it.type,
            label = it.label,
            readOnly = it.readOnly ?: false,
            repeatable = it.repeatable ?: false,
            removable = it.removable ?: false,
            subType = it.subType,
            isMulti = it.isMulti ?: false,
            values = it.values?.toValueDomain(),
            conditional = it.conditional?.toConditionalDomain(),
            _components = it.components?.toInitComponentDomain(),
            injected_value = it.injected_value,
            logics = it.logics?.toInitLogicDomain()
        )
    }
}

fun List<InitLogic>.toInitLogicDomain(): List<InitLogicDomain> {
    return map {
        InitLogicDomain(
            it.logicType,
            it.experssions?.toInitExpressionDomain(),
        )
    }
}

fun List<InitExpression>.toInitExpressionDomain(): List<ExpressionDomain> {
    return map {
        ExpressionDomain(it.conditions?.toInitConditionDomain())
    }
}

fun List<InitCondition>.toInitConditionDomain(): List<ConditionDomain> {
    return map {
        ConditionDomain(
            it.firstFieldKey,
            it.secondFieldKey,
            it.firstOperator?.toInitOperatorDomain(),
            it.secondOperator?.toInitOperatorDomain(),
            value = it.value,
            values = it.values
        )
    }
}

fun InitOperator.toInitOperatorDomain(): OperatorDomain {
    return OperatorDomain(this.title, this.symbol)
}

fun List<InstanceTicketsBasicInformationValues>.toInitFormDomainList(): List<InstanceTicketsBasicInformationValuesDomain> {
    return map {
        InstanceTicketsBasicInformationValuesDomain(it.key, it.value)
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
            _components = it.components?.toComponentDomain(),
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
           id =  this.id,
           key = this.key,
           layout =  this.layout,
           subtype =  this.subtype,
           required =  this.required,
           maxLength =  this.maxLength,
           minLength =  this.minLength,
           pattern =  this.pattern,
           maxTotalSize =  this.maxTotalSize,
           maxFileNumber =  this.maxFileNumber,
           max = this.max,
           min =  this.min,
           blacklistAttachment =  this.blacklistAttachment,
           attachedValidationType =  this.attachedValidationType,
           whitelistAttachment =  this.whitelistAttachment,
           domainType =  this.domainType,
           domainList =  this.domainList,
           validationType =  this.validationType,
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
            components = it.components.value?.toComponent(),
            logics = it.logics?.toLogic()
        )
    }.updateComponentTypesReverse()
}


fun LayoutDomain.toLayout(): Layout {
    return Layout(this.row, this.columns)
}

fun ValidateDomain.toValidate(): Validate {
    return Validate(id =  this.id,
        key = this.key,
        layout =  this.layout,
        subtype =  this.subtype,
        required =  this.required,
        maxLength =  this.maxLength,
        minLength =  this.minLength,
        pattern =  this.pattern,
        maxTotalSize =  this.maxTotalSize,
        maxFileNumber =  this.maxFileNumber,
        max = this.max,
        min =  this.min,
        blacklistAttachment =  this.blacklistAttachment,
        attachedValidationType =  this.attachedValidationType,
        whitelistAttachment =  this.whitelistAttachment,
        domainType =  this.domainType,
        domainList =  this.domainList,
        validationType =  this.validationType)

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

/*fun InstanceTicketsBasicInformationValues.toInitFormList(): List<InitForm> {
    val initFormList = mutableListOf<InitForm>()


    initFormList.add(InitForm("title", title))
    initFormList.add(InitForm("region", region))
    initFormList.add(InitForm("vendor", vendor))
    initFormList.add(InitForm("activity_type", activity_type))
    initFormList.add(InitForm("rollback_plan", rollback_plan))
    initFormList.add(InitForm("subcontractor", subcontractor))
    initFormList.add(InitForm("logical_access", logical_access))
    initFormList.add(InitForm("need_kpi_check", need_kpi_check))
    initFormList.add(InitForm("service_affect", service_affect))
    initFormList.add(InitForm("datetime_ea1pqd", datetime_ea1pqd))
    initFormList.add(InitForm("physical_access", physical_access))
    initFormList.add(InitForm("physical_change", physical_change))
    initFormList.add(InitForm("planned_end_time", planned_end_time))
    initFormList.add(InitForm("uat_descriptions", uatDescriptions))
    initFormList.add(InitForm("implementer_group", implementer_group))
    initFormList.add(InitForm("objective_product", objective_product))
    initFormList.add(InitForm("planned_start_time", planned_start_time))
    initFormList.add(InitForm("other_objective_nes", other_objective_nes))
    initFormList.add(InitForm("planned_time_details", planned_time_details))
    initFormList.add(InitForm("planned_time_duration_day", planned_time_duration_day))
    initFormList.add(InitForm("brief_description_of_change", brief_description_of_change))
    initFormList.add(InitForm("mtncounterpart_of_implementer", mtncounterpart_of_implementer))
    initFormList.add(InitForm("impacted_vip_or_vvip_site_list", impacted_vip_or_vvip_site_list))
    initFormList.add(InitForm("name_of_implementation_support", name_of_implementation_support))
    initFormList.add(InitForm("work_plan_and_sequence_of_actions", work_plan_and_sequence_of_actions))
    initFormList.add(InitForm("phone_no_of_implementation_support", phone_no_of_implementation_support))
    initFormList.add(InitForm("post_implementation_actions_and_plans", post_implementation_actions_and_plans))
    initFormList.add(InitForm("reason_requirement_of_cr_implementation", reason_requirement_of_cr_implementation))
    initFormList.add(InitForm("is_shared_account", is_shared_account?:""))
    initFormList.add(InitForm("implementer_node_user_account", implementer_node_user_account?:""))


    initFormList.add(
        InitForm(
            "mtni_manager_approval_for_using_shared_account",
            mtni_manager_approval_for_using_shared_account?.joinToString(",") ?: ""
        )
    )

    if (province.isNotEmpty()) {
        initFormList.add(
            InitForm(
                "province",
                province.joinToString(",") { it }
            )
        )
    }


    new_e_tilt?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "new_e_tilt",
                    it.joinToString(",") { jsonElement -> jsonElement.toString() }
                )
            )
        }
    }
    new_height?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "new_height",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    new_m_tilt?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "new_m_tilt",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    val observerList = cr_observer.toStringList()
    if (observerList.isNotEmpty()) {
        initFormList.add(
            InitForm(
                "cr_observer",
                observerList.joinToString(",")
            )
        )
    }
    new_azimuth?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "new_azimuth",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    related_oss?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "related_oss",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    sector_name?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "sector_name",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    Objective_nes?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "Objective_nes",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    freq_band_mhz?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "freq_band_mhz",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    original_e_tilt?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "original_e_tilt",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    original_height?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "original_height",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    original_m_tilt?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "original_m_tilt",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    original_azimuth?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "original_azimuth",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }
    textfield_89b2yi?.let {
        if (it.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "textfield_89b2yi",
                    it.joinToString(",") { je -> je.toString() }
                )
            )
        }
    }


    script_and_documents?.toStringList()?.let { list ->
        if (list.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "script_and_documents",
                    list.joinToString(",")
                )
            )
        }
    }
    uat_document_attachment?.toStringList()?.let { list ->
        if (list.isNotEmpty()) {
            initFormList.add(
                InitForm(
                    "uat_document_attachment",
                    list.joinToString(",")
                )
            )
        }
    }

    return initFormList
}*/




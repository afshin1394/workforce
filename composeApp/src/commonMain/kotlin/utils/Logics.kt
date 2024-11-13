package utils


import data.network.response.task.logic.LogicDomain
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import domain.models.form_struct.logic.ConditionDomain
import domain.models.ticket.PhaseDomain
import domain.models.ticket.TicketDetailRequestDomain
import domain.usecase.usecase.ticket.GetTicketDetailsUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.DatePickerFormat.format
import irancell.nwg.wfm.DateTime
import irancell.nwg.wfm.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import presentation.model.ExtractLogicsModel

class LogicCalculation(
    private val viewModelScope: CoroutineScope,
    private val allComponents: List<ComponentDomain>
) : KoinComponent {
    val getTicketDetailsUseCase: GetTicketDetailsUseCase by inject()
    lateinit var ticketId: String
    val validationErrorList = mutableListOf<ExtractLogicsModel>()

    val affectedComponents = mutableListOf<ComponentDomain>()


    suspend fun extractRequiredAndValidateLogics(component: ComponentDomain): MutableList<ExtractLogicsModel> = coroutineScope {
        if (!component.processLogicDomain.value.shouldHide) {
                component.logics?.forEach { logic ->
                    var hasLogic = false
                    val typeLogic: String
                    val idCmp = component.id ?: ""

                    when (logic.logicType) {


                        LogicType.Required -> {
                            typeLogic = LogicType.Required
                            val expressionSatisfied =
                                evaluateLogics(component, logic) && !component.hasValue()
                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(required = expressionSatisfied)
                            )

                            if (expressionSatisfied) {
                                hasLogic = true
                                validationErrorList.add(
                                    ExtractLogicsModel(
                                        hasLogic,
                                        typeLogic,
                                        idCmp
                                    )
                                )
                            }
                        }

                        LogicType.Validate -> {
                            typeLogic = LogicType.Validate
                            val expressionSatisfied =
                                evaluateValidateLogics(allComponents, component, logic)

                            expressionSatisfied?.let {
                                component.updateProcessLogicDomain(
                                    component.processLogicDomain.value.copy(
                                        validate = it.result,
                                        errorMessage = it.message
                                    )
                                )
                                hasLogic = it.result
                            } ?: component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(
                                    validate = false,
                                    errorMessage = null
                                )
                            )

                            if (hasLogic) validationErrorList.add(
                                ExtractLogicsModel(
                                    hasLogic,
                                    typeLogic,
                                    idCmp
                                )
                            )
                        }

                        LogicType.Disable -> {
                            val expressionSatisfied = evaluateLogics(component, logic)
                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(disabled = expressionSatisfied)
                            )
                            hasLogic = expressionSatisfied
                        }

                        LogicType.ReadOnly -> {
                            val expressionSatisfied = evaluateLogics(component, logic)
                            component.updateProcessLogicDomain(
                                component.processLogicDomain.value.copy(readOnly = expressionSatisfied)
                            )
                            hasLogic = expressionSatisfied
                        }


                    }
                }

            }

        return@coroutineScope validationErrorList

    }


    suspend fun extractHideLogic(component: ComponentDomain): MutableList<ExtractLogicsModel> =
        coroutineScope {
            validationErrorList.clear()

            component.logics?.forEach { logic ->
                var hasLogic = false
                val typeLogic: String
                val idCmp = component.id ?: ""

                when (logic.logicType) {
                    LogicType.Hide -> {
                        typeLogic = LogicType.Hide
                        val expressionSatisfied = evaluateLogics(component, logic)

                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(shouldHide = expressionSatisfied)
                        )
                        if (expressionSatisfied) {

                            if (component.type == FormViewerTypes.ImageView) {
                                hasLogic = true
                                validationErrorList.add(
                                    ExtractLogicsModel(
                                        hasLogic,
                                        typeLogic,
                                        component.id ?: "",
                                        component.key ?: ""
                                    )
                                )
                            }

                            component.clearValues()

                        }

                        component.components.value?.forEach { nestedComponent ->
                            nestedComponent.updateProcessLogicDomain(
                                nestedComponent.processLogicDomain.value.copy(shouldHide = expressionSatisfied)
                            )
                            if (expressionSatisfied) {
                                if (nestedComponent.type == FormViewerTypes.ImageView) {
                                    hasLogic = true
                                    validationErrorList.add(
                                        ExtractLogicsModel(
                                            hasLogic,
                                            typeLogic,
                                            nestedComponent.id ?: "",
                                            nestedComponent.key ?: ""
                                        )
                                    )
                                }
                                nestedComponent.clearValues()
                            }
                        }

                        hasLogic = expressionSatisfied
                    }


                }

            }
            return@coroutineScope validationErrorList

        }

    suspend fun extractOfflineValueModifierLogics(component: ComponentDomain) {
        if (!component.processLogicDomain.value.shouldHide) {

            component.logics?.forEach { logic ->

                when (logic.logicType) {
                    LogicType.Calculate -> {
                        val result = checkCalculation(logic)

                        val updatedValue = result ?: ""
                        component.values = listOf(
                            updateValueDomain(
                                component.values?.getOrNull(0) ?: ValueDomain(), updatedValue
                            )
                        )
                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(calculatedValue = updatedValue)
                        )
                    }

                    LogicType.Bind -> {
                        val listOfBinding = logic.getListOfBindingComponents()

                        val updatedValue = listOfBinding.getOrNull(0) ?: ""
                        component.values = listOf(
                            updateValueDomain(
                                component.values?.getOrNull(0) ?: ValueDomain(), updatedValue
                            )
                        )
                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(calculatedValue = updatedValue)
                        )
                    }
                }
            }
        }
    }

    suspend fun extractValueModifierLogics(component: ComponentDomain) {
        var shouldBreak = false
        component.logics?.forEach { logic ->
            when (logic.logicType) {

                LogicType.Ticket_Auto_Fill -> {
                    if (!component.processLogicDomain.value.shouldHide) {


                        for (logic in component.logics) {

                            for (option in logic.ticketAutoFillLogicDomain?.options.orEmpty()) {
                                if(shouldBreak) break
                                val phaseName = option.phaseName
                                val property = option.property

                                if (phaseName != null && property != null) {
                                    // Set isAutoFillLoading to true
                                    component.updateProcessLogicDomain(
                                        component.processLogicDomain.value.copy(
                                            isAutoFillLoading = true
                                        )
                                    )


                                        getTicketDetailsUseCase(
                                            Pair(
                                                ticketId,
                                                TicketDetailRequestDomain(
                                                    listOf(PhaseDomain(phaseName, property))
                                                )
                                            )
                                        ).collect { result ->

                                            when (result.status) {
                                                AsyncStatus.EMPTY -> {
                                                    // Handle empty case if needed
                                                }

                                                AsyncStatus.ERROR -> {
                                                    // Handle error case if needed
                                                }

                                                AsyncStatus.LOADING -> {
                                                    // Handle loading case if needed
                                                }

                                                AsyncStatus.SUCCESS -> {
                                                    result.data?.keys?.forEach { key ->
                                                        result.data[key]?.let { resultData ->
                                                            var value = resultData

                                                            if (resultData.isNotEmpty()) {
                                                                if (component.type == FormViewerTypes.Datetime ||
                                                                    component.type == FormViewerTypes.Time ||
                                                                    component.type == FormViewerTypes.Date
                                                                ) {
                                                                    value =
                                                                        resultData.parsServerDateTime()
                                                                }

                                                                val updatedValueDomain =
                                                                    updateValueDomain(
                                                                        component.values?.get(0)
                                                                            ?: ValueDomain(),
                                                                        value
                                                                    )

                                                                // Update component values
                                                                component.updateValues(
                                                                    listOf(updatedValueDomain)
                                                                )

                                                                // Update process logic domain
                                                                component.updateProcessLogicDomain(
                                                                    component.processLogicDomain.value.copy(
                                                                        calculatedValue = value,
                                                                    )
                                                                )
                                                                shouldBreak = true
                                                                // Set flag to break out of all loops
                                                                return@collect
                                                            }
                                                        }
                                                    }
                                                }

                                        }

                                }
                            }
                        }


                        component.updateProcessLogicDomain(
                            component.processLogicDomain.value.copy(
                                isAutoFillLoading = false,
                            )
                        )
                    }
                }
                }
            }
        }
    }


    private fun LogicDomain.getListOfBindingComponents(): List<String> {
        val list: ArrayList<String> = arrayListOf()
        this.bind_logic?.field_options?.let {
            it.forEach { fieldOption ->
                val cmp = allComponents.findComponentByKey(fieldOption.field_key)
                cmp?.let { it1 -> affectedComponents.add(it1) }
                cmp?.values?.getOrNull(0)?.value?.let { value ->
                    if (value.isNotEmpty())
                        list.add(value)
                }

            }
        }
        return list
    }

    fun evaluateValidateLogics(
        components: List<ComponentDomain>,
        componentDomain: ComponentDomain,
        logicDomain: LogicDomain
    ): ValidateLogicResult? {
        val results = checkValidateLogicsExpression(components, componentDomain, logicDomain)
        Napier.log(LogLevel.ASSERT, tag = "evaluateValidateLogics", message = results.toString())
        return results.aggregateValidateLogicResult()
    }

    fun evaluateLogics(component: ComponentDomain, logicDomain: LogicDomain): Boolean {
        val results = checkExpression(component, logicDomain)
        Napier.log(LogLevel.ASSERT, tag = "evaluateLogics", message = results.toString())
        return results.calculateResult()
    }


    private fun ArrayList<ArrayList<Boolean>>.calculateResult(): Boolean {
        return try {
            this.map { innerList ->
                innerList.reduce { acc, b -> acc && b }
            }.reduce { acc, b -> acc || b }
        } catch (exception: Exception) {
            return false
        }
    }


    private fun checkCalculation(it: LogicDomain): String? {
        it.experssions?.forEach { exp ->
            exp.conditions?.forEach { cond ->

                when (cond.secondOperator?.title) {
                    OperatorType.Add -> {
                        return invokeOperation(OperatorType.Add, cond)

                    }

                    OperatorType.Subtract -> {
                        return invokeOperation(OperatorType.Subtract, cond)

                    }

                    OperatorType.Divide -> {
                        return invokeOperation(OperatorType.Divide, cond)

                    }

                    OperatorType.Multiply -> {
                        return invokeOperation(OperatorType.Multiply, cond)

                    }

                }

            }

        }
        return null
    }

    fun invokeOperation(
        operator: String,
        cond: ConditionDomain,
    ): String? {
        val component1 = allComponents.findComponentByKey(cond.firstFieldKey)
        val component2 = allComponents.findComponentByKey(cond.secondFieldKey)
        component1?.let { affectedComponents.add(it) }
        component2?.let { affectedComponents.add(it) }
        val value1 = component1?.getValueBaseOnType()
        val value2 = component2?.getValueBaseOnType()

        value1?.let { v1 ->
            value2?.let { v2 ->
                when (operator) {
                    OperatorType.Add -> {
                        return (v1 + v2).toStringOrEmptyString(component1.type == FormViewerTypes.Datetime && component2.type == FormViewerTypes.Datetime)
                    }

                    OperatorType.Subtract -> {
                        return (v1 - v2).toStringOrEmptyString(component1.type == FormViewerTypes.Datetime && component2.type == FormViewerTypes.Datetime)
                    }

                    OperatorType.Multiply -> {
                        return (v1 * v2).toStringOrEmptyString(component1.type == FormViewerTypes.Datetime && component2.type == FormViewerTypes.Datetime)
                    }

                    OperatorType.Divide -> {
                        if (v2 == 0.0)
                            return ""
                        else
                            return (v1 / v2).toStringOrEmptyString(component1.type == FormViewerTypes.Datetime && component2.type == FormViewerTypes.Datetime)
                    }

                    else -> {
                        return null
                    }
                }

            }
        }
        return null
    }


    private fun checkExpression(
        currentComponent: ComponentDomain,
        it: LogicDomain
    ): ArrayList<ArrayList<Boolean>> {
        val expressionResults: ArrayList<ArrayList<Boolean>> = arrayListOf()
        it.experssions?.forEachIndexed { i, expression ->
            expressionResults.add(arrayListOf())
            expression.conditions?.forEachIndexed { j, condition ->
                val component = allComponents.findComponentByKey(condition.firstFieldKey)
                component?.let { it1 -> affectedComponents.add(it1) }


                when (condition.secondOperator?.title) {
                    OperatorType.IsFill -> {
                        component?.values?.let {
                            expressionResults[i].add(it.getOrNull(0)?.value?.isNotEmpty() == true)
                        }
                    }

                    OperatorType.IsBlank -> {
                        if (component?.values == null) {
                            expressionResults[i].add(true)
                        } else {
                            component.values?.let {
                                expressionResults[i].add(it.getOrNull(0)?.value?.isEmpty() == true)
                            }
                        }
                    }

                    OperatorType.Equals -> {
                        if (component?.isSelectableValue() == true) {
                            component.values?.forEach {
                                if (it.isSelected && it.value.equals(condition.value))
                                    expressionResults[i].add(true)
                            }
                        } else {
                            component?.values?.getOrNull(0)?.let {
                                if (it.value.equals(condition.value))
                                    expressionResults[i].add(true)
                            }
                        }

                    }

                    OperatorType.NotEquals -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.forEach {
                                        if (it.isSelected && !(it.value.equals(
                                                condition.value
                                            ))
                                        )
                                            expressionResults[i].add(true)
                                    }
                                } else {
                                    component.values?.getOrNull(0)?.let {
                                        if (!it.value.equals(condition.value))
                                            expressionResults[i].add(true)
                                    }
                                }
                            }
                        }
                    }

                    OperatorType.Contains -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.forEach {

                                        expressionResults[i].add(
                                            it.isSelected && !(it.value.equals(
                                                condition.value
                                            ))
                                        )
                                    }
                                } else {
                                    component.values?.getOrNull(0)?.let {
                                        expressionResults[i].add(!it.value.equals(condition.value))
                                    }
                                }

                            }
                        }
                    }

                    OperatorType.StartWith -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.forEach {

                                        expressionResults[i].add(
                                            it.isSelected && it.value?.startsWith(
                                                condition.value.toString()
                                            ) == true
                                        )
                                    }
                                } else {
                                    component.values?.let {
                                        if (it.isNotEmpty()) {
                                            if (it.getOrNull(0)?.value?.startsWith(
                                                    condition.value.toString()
                                                ) == true
                                            )
                                                expressionResults[i].add(true)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OperatorType.GreaterThan -> {
                        component?.values?.let { it ->
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.filter { it.isSelected && it.value?.isNotEmpty() == true }
                                        ?.forEach { valueDomain ->
                                            valueDomain.value?.toFloatOrNull()
                                                ?.let { componentValue ->
                                                    condition.value?.toFloatOrNull()
                                                        ?.let { conditionValue ->
                                                            expressionResults[i].add(
                                                                componentValue > conditionValue
                                                            )
                                                        }
                                                }
                                        }
                                } else {
                                    component.values?.getOrNull(0)?.let { valueDomain ->
                                        valueDomain.value?.toFloatOrNull()?.let { componentValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    expressionResults[i].add(
                                                        componentValue > conditionValue
                                                    )
                                                }
                                        }
                                    }
                                }
                            }
                        }

                    }

                    OperatorType.GreaterThanOrEqualsTo -> {
                        component?.values?.let { it ->
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.filter { it.isSelected && it.value?.isNotEmpty() == true }
                                        ?.forEach { valueDomain ->
                                            valueDomain.value?.toFloatOrNull()
                                                ?.let { componentValue ->
                                                    condition.value?.toFloatOrNull()
                                                        ?.let { conditionValue ->
                                                            expressionResults[i].add(
                                                                componentValue >= conditionValue
                                                            )
                                                        }
                                                }
                                        }
                                } else {
                                    component.values?.getOrNull(0)?.let { valueDomain ->
                                        valueDomain.value?.toFloatOrNull()?.let { componentValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    expressionResults[i].add(
                                                        componentValue >= conditionValue
                                                    )
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OperatorType.LessThan -> {
                        component?.values?.let { it ->
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.filter { it.isSelected && it.value?.isNotEmpty() == true }
                                        ?.forEach { valueDomain ->
                                            valueDomain.value?.toFloatOrNull()
                                                ?.let { componentValue ->
                                                    condition.value?.toFloatOrNull()
                                                        ?.let { conditionValue ->
                                                            expressionResults[i].add(
                                                                componentValue < conditionValue
                                                            )
                                                        }
                                                }
                                        }
                                } else {
                                    component.values?.getOrNull(0)?.let { valueDomain ->
                                        valueDomain.value?.toFloatOrNull()?.let { componentValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    expressionResults[i].add(
                                                        componentValue < conditionValue
                                                    )
                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OperatorType.LessThanOrEqualsTo -> {
                        component?.values?.let { it ->
                            if (it.isNotEmpty()) {
                                if (component.isSelectableValue()) {
                                    component.values?.filter { it.isSelected && it.value?.isNotEmpty() == true }
                                        ?.forEach { valueDomain ->
                                            valueDomain.value?.toFloatOrNull()
                                                ?.let { componentValue ->
                                                    condition.value?.toFloatOrNull()
                                                        ?.let { conditionValue ->
                                                            expressionResults[i].add(
                                                                componentValue <= conditionValue
                                                            )
                                                        }
                                                }
                                        }
                                } else {
                                    component.values?.getOrNull(0)?.let { valueDomain ->
                                        valueDomain.value?.toFloatOrNull()?.let { componentValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    expressionResults[i].add(
                                                        componentValue <= conditionValue
                                                    )

                                                }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    OperatorType.ContainsAny -> {

                        condition.values?.let {

                            expressionResults[i].add(component?.values?.filter { it.value?.isNotEmpty() == true && it.isSelected }
                                ?.map { it.value }?.intersect(condition.values)
                                ?.isNotEmpty() == true
                            )

                        }

                    }

                    OperatorType.NotContainsAny -> {
                        condition.values?.let {

                            expressionResults[i].add(component?.values?.filter { it.value?.isNotEmpty() == true && it.isSelected }
                                ?.map { it.value }?.intersect(condition.values)
                                ?.isEmpty() == true
                            )
                        }
                    }

                    OperatorType.ContainsAll -> {
                        condition.values?.let {

                            expressionResults[i].add(component?.values?.filter { it.value?.isNotEmpty() == true && it.isSelected }
                                ?.map { it.value }?.containsAll(condition.values) == true)
                        }
                    }
                }
            }
        }
        Napier.log(
            LogLevel.ASSERT,
            tag = "expressionResults",
            message = expressionResults.toString()
        )
        return expressionResults
    }


    private fun List<ComponentDomain>.findComponentByKey(firstFieldKey: String?): ComponentDomain? {
        for (component in this) {
            if (component.key == firstFieldKey) {
                return component
            }
            component.components.value?.findComponentByKey(firstFieldKey)?.let { return it }
        }
        return null
    }

    private fun List<ComponentDomain>.findComponentById(id: String?): ComponentDomain? {
        for (component in this) {
            if (component.id == id) {
                return component
            }
            component.components.value?.findComponentByKey(id)?.let { return it }
        }
        return null
    }


    data class ValidateLogicResult(val result: Boolean, val message: ResourceFormattedStringDesc) {
        override fun toString(): String {
            return "ValidateLogicResult(result=$result, message=$message)"
        }
    }


    private fun checkValidateLogicsExpression(
        components: List<ComponentDomain>,
        componentDomain: ComponentDomain, it: LogicDomain
    ): ArrayList<ArrayList<ValidateLogicResult>> {
        val expressionResults: ArrayList<ArrayList<ValidateLogicResult>> = arrayListOf()
        it.experssions?.forEachIndexed { i, expression ->
            expressionResults.add(arrayListOf())
            expression.conditions?.forEachIndexed { j, condition ->
                val cmp = allComponents.findComponentByKey(condition.firstFieldKey)
                cmp?.let { it1 -> affectedComponents.add(it1) }
                val value = if (condition.firstFieldKey == "now") {
                    getCurrentDateLocalDateTime().localDateTimeToMilliseconds().toString()
                } else {
                    cmp?.values?.getOrNull(0)?.value
                }
                condition.firstOperator?.let {
                    when (condition.firstOperator.title) {
                        OperatorType.Equals -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue == currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotEqual,
                                                                        (fieldValue + conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }

                                    }

                                    OperatorType.Subtract -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue == currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotEqual,
                                                                        (fieldValue - conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Multiply -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue == currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotEqual,
                                                                        (fieldValue * conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Divide -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue == currentValue,
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.NotEqual,
                                                                            (fieldValue / conditionValue).toString()
                                                                        )
                                                                    )
                                                                )

                                                        }
                                                }
                                        }
                                    }


                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue: Double? =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.getOrNull(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()?.toDouble()
                                    } else {
                                        componentDomain.values?.getOrNull(0)?.value?.toDoubleOrNull()
                                    }
                                cmpValue?.let {

                                    expressionResults[i].add(
                                        ValidateLogicResult(
                                            (cmpValue ?: 0.0
                                                    ) == (value?.toDoubleOrNull() ?: 0.0),
                                            StringDesc.ResourceFormatted(
                                                MR.strings.NotEqual,
                                                (value).toString()
                                            )
                                        )
                                    )
                                }

                            }
                        }

                        OperatorType.NotEquals -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue != currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.Equal,
                                                                        (fieldValue + conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }

                                    }

                                    OperatorType.Subtract -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    (fieldValue - conditionValue != currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.Equal,
                                                                        (fieldValue - conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Multiply -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue != currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.Equal,
                                                                        (fieldValue * conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Divide -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue != currentValue,
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.Equal,
                                                                            (fieldValue / conditionValue).toString()
                                                                        )
                                                                    )
                                                                )

                                                        }
                                                }
                                        }
                                    }

                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue: Double? =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.getOrNull(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()?.toDouble()
                                    } else {
                                        componentDomain.values?.getOrNull(0)?.value?.toDoubleOrNull()
                                    }
                                cmpValue?.let {

                                    expressionResults[i].add(
                                        ValidateLogicResult(
                                            (cmpValue ?: 0.0
                                                    ) != (value?.toDouble() ?: 0.0),
                                            StringDesc.ResourceFormatted(
                                                MR.strings.Equal,
                                                (value).toString()
                                            )
                                        )
                                    )
                                }

                            }
                        }

                        OperatorType.GreaterThan -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue + conditionValue > currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotGreaterThan,
                                                                        (fieldValue + conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }

                                    }

                                    OperatorType.Subtract -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue - conditionValue > currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotGreaterThan,
                                                                        (fieldValue - conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Multiply -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue * conditionValue > currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotGreaterThan,
                                                                        (fieldValue * conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Divide -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        !(fieldValue / conditionValue > currentValue),
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.NotGreaterThan,
                                                                            (fieldValue / conditionValue).toString()
                                                                        )
                                                                    )
                                                                )

                                                        }
                                                }
                                        }
                                    }

                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue: Double? =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.getOrNull(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()?.toDouble()
                                    } else {
                                        componentDomain.values?.getOrNull(0)?.value?.toDoubleOrNull()
                                    }
                                if (componentDomain.type == FormViewerTypes.Datetime) {
                                    expressionResults[i].add(
                                        ValidateLogicResult(
                                            ((cmpValue
                                                ?: 0.0) > (value?.toDoubleOrNull() ?: 0.0)),
                                            StringDesc.ResourceFormatted(
                                                MR.strings.NotGreaterThan,
                                                (getLocalDateTimeFromLong(
                                                    value?.toLong() ?: 0L
                                                ).format("yyyy-MM-dd HH:mm:ss"))
                                            )
                                        )
                                    )
                                }
                            }
                        }

                        OperatorType.GreaterThanOrEqualsTo -> {

                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue + conditionValue > currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotGreaterOrEqualTo,
                                                                        (fieldValue + conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }

                                    }

                                    OperatorType.Subtract -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue - conditionValue > currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotGreaterOrEqualTo,
                                                                        (fieldValue - conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Multiply -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue * conditionValue > currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotGreaterOrEqualTo,
                                                                        (fieldValue * conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Divide -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        !(fieldValue / conditionValue > currentValue),
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.NotGreaterOrEqualTo,
                                                                            (fieldValue / conditionValue).toString()
                                                                        )
                                                                    )
                                                                )

                                                        }
                                                }
                                        }
                                    }

                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue: Double? =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.getOrNull(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()?.toDouble()
                                    } else {
                                        componentDomain.values?.getOrNull(0)?.value?.toDoubleOrNull()
                                    }
                                cmpValue?.let {

                                    expressionResults[i].add(
                                        ValidateLogicResult(
                                            !((cmpValue ?: 0.0
                                                    ) >= (value?.toDoubleOrNull() ?: 0.0)),
                                            StringDesc.ResourceFormatted(
                                                MR.strings.NotGreaterOrEqualTo,
                                                (value).toString()
                                            )
                                        )
                                    )
                                }
                            }

                        }

                        OperatorType.LessThan -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue + conditionValue < currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotLessThan,
                                                                        (fieldValue + conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }

                                    }

                                    OperatorType.Subtract -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue - conditionValue < currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotLessThan,
                                                                        (fieldValue - conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Multiply -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue * conditionValue < currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotLessThan,
                                                                        (fieldValue * conditionValue).toString()
                                                                    )
                                                                )
                                                            )
                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Divide -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.values?.getOrNull(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        !(fieldValue / conditionValue < currentValue),
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.NotLessThan,
                                                                            (fieldValue / conditionValue).toString()
                                                                        )
                                                                    )
                                                                )

                                                        }
                                                }
                                        }
                                    }

                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue: Double? =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.getOrNull(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()?.toDouble()
                                    } else {
                                        componentDomain.values?.getOrNull(0)?.value?.toDoubleOrNull()
                                    }
                                expressionResults[i].add(
                                    ValidateLogicResult(
                                        ((cmpValue
                                            ?: 0.0) < (value?.toDoubleOrNull() ?: 0.0)),
                                        StringDesc.ResourceFormatted(
                                            MR.strings.NotLessThan,
                                            (value).toString()
                                        )
                                    )
                                )
                            }
                        }

                        OperatorType.LessThanOrEqualsTo -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue + conditionValue <= currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotLessThanOrEqualTo,
                                                                        (fieldValue + conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }

                                    }

                                    OperatorType.Subtract -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue - conditionValue <= currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotLessThanOrEqualTo,
                                                                        (fieldValue - conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Multiply -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    !(fieldValue * conditionValue <= currentValue),
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.NotLessThanOrEqualTo,
                                                                        (fieldValue * conditionValue).toString()
                                                                    )
                                                                )
                                                            )

                                                        }
                                                }
                                        }
                                    }

                                    OperatorType.Divide -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.value?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.getOrNull(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        !(fieldValue / conditionValue <= currentValue),
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.NotLessThanOrEqualTo,
                                                                            (fieldValue / conditionValue).toString()
                                                                        )
                                                                    )
                                                                )

                                                        }
                                                }
                                        }
                                    }

                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue: Double? =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.getOrNull(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()?.toDouble()
                                    } else {
                                        componentDomain.values?.getOrNull(0)?.value?.toDoubleOrNull()
                                    }
                                cmpValue?.let {
                                    expressionResults[i].add(
                                        ValidateLogicResult(
                                            ((cmpValue
                                                ?: 0.0) <= (value?.toDoubleOrNull() ?: 0.0)),
                                            StringDesc.ResourceFormatted(
                                                MR.strings.NotLessThanOrEqualTo,
                                                (getLocalDateTimeFromLong(
                                                    value?.toLong() ?: 0L
                                                ).format("yyyy-MM-dd HH:mm:ss")).toString()
                                            )
                                        )
                                    )
                                }
                            }
                        }


                        else -> {

                        }
                    }

                }


            }
        }
        Napier.log(
            LogLevel.ASSERT,
            tag = "expressionResults",
            message = expressionResults.toString()
        )
        return expressionResults
    }


    fun ArrayList<ArrayList<ValidateLogicResult>>.aggregateValidateLogicResult(): ValidateLogicResult? {
        return this[0].firstOrNull { it.result }
    }

    private fun ComponentDomain.hasValue(): Boolean {
        return if (this.isSelectableValue()) {
            this.values?.any { it.isSelected } ?: false
        } else {
            this.values?.getOrNull(0)?.value?.isNotEmpty() == true
        }
    }

    fun ComponentDomain.isSelectableValue() =
        this.type == FormViewerTypes.Checklist || this.type == FormViewerTypes.Radio || this.type == FormViewerTypes.Select || this.type == FormViewerTypes.Multi


    fun ticketId(ticketId: String) {
        this.ticketId = ticketId
    }

    private fun ComponentDomain?.clearValues() {
        if (this?.isSelectableValue() == true) {
            this.updateValues(this.values?.map {
                it.isSelected = false
                it
            })
        } else
            this?.updateValues(null)
    }
}





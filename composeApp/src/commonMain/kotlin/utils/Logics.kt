package utils

import androidx.compose.runtime.Composable
import data.network.response.task.logic.LogicDomain
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import domain.models.form_struct.logic.ConditionDomain
import domain.models.ticket.PhaseDomain
import domain.models.ticket.TicketDetailRequestDomain
import domain.usecase.ResultStatus
import domain.usecase.usecase.photo.DeletePhotoByComponentKeyAndIdUseCase
import domain.usecase.usecase.ticket.GetTicketDetailsUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
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





    suspend fun extractLogics(
        components: List<ComponentDomain>,
        component: ComponentDomain
    ): MutableList<ExtractLogicsModel> = coroutineScope {
        var hasLogic = false
        var typeLogic = ""
        var idCmp = ""
        viewModelScope.launch {
            component.logics?.forEach { it ->

                when (it.logicType) {
                    LogicType.Hide -> {
                        typeLogic = LogicType.Hide
                        val expressionSatisfied = evaluateLogics(components, it)
                        val cmp = allComponents.findComponentById(component.id)
                        Napier.log(LogLevel.ASSERT, "Hide", message = cmp.toString())
//                        cmp?.processLogicDomain?.value?.shouldHide = expressionSatisfied
                        cmp?.updateProcessLogicDomain(cmp.processLogicDomain.value.copy(shouldHide = expressionSatisfied))

                        if (expressionSatisfied) {
                            cmp.clearValues()
                        }
                        cmp?.components?.value?.forEach {
                            it.updateProcessLogicDomain(cmp.processLogicDomain.value.copy(shouldHide = expressionSatisfied))
                            if (expressionSatisfied)
                                it.clearValues()
                        }


                        if (expressionSatisfied)
                            hasLogic = true

                    }

                    LogicType.Required -> {
                        typeLogic = LogicType.Required
                        val expressionSatisfied = evaluateLogics(components, it)
                        val cmp = allComponents.findComponentById(component.id)
                        idCmp = component.id ?: ""
                        cmp?.updateProcessLogicDomain(cmp.processLogicDomain.value.copy(required = expressionSatisfied))


                        hasLogic = expressionSatisfied
                    }

                    LogicType.Disable -> {
                        typeLogic = LogicType.Disable
                        val expressionSatisfied = evaluateLogics(components, it)
                        val cmp = allComponents.findComponentById(component.id)
                        cmp?.updateProcessLogicDomain(cmp.processLogicDomain.value.copy(disabled = expressionSatisfied))
                        hasLogic = expressionSatisfied
                    }

                    LogicType.ReadOnly -> {
                        typeLogic = LogicType.ReadOnly
                        val expressionSatisfied = evaluateLogics(components, it)
                        val cmp = allComponents.findComponentById(component.id)
                        cmp?.updateProcessLogicDomain(cmp.processLogicDomain.value.copy(readOnly = expressionSatisfied))

                        hasLogic = expressionSatisfied
                    }


                    LogicType.Calculate -> {
                        typeLogic = LogicType.Calculate
                        val result = checkCalculation(components, it)
                        val cmp = allComponents.findComponentById(component.id)
                        result?.let { res ->
                            val updatedValueDomain = updateValueDomain(
                                cmp?.values?.get(0) ?: ValueDomain(),
                                result
                            )
                            cmp?.values = listOf(updatedValueDomain)
                            cmp?.updateProcessLogicDomain(
                                cmp.processLogicDomain.value.copy(
                                    calculatedValue = result
                                )
                            )
                            hasLogic = result.isNotEmpty()

                        } ?: run {
                            val updatedValueDomain = updateValueDomain(
                                cmp?.values?.get(0) ?: ValueDomain(),
                                ""
                            )
                            cmp?.values = listOf(updatedValueDomain)
                            cmp?.updateProcessLogicDomain(
                                cmp.processLogicDomain.value.copy(
                                    calculatedValue = ""
                                )
                            )
                        }

                    }

                    LogicType.Validate -> {
                        typeLogic = LogicType.Validate
                        val expressionSatisfied = evaluateValidateLogics(components, component, it)
                        val cmp = allComponents.findComponentById(component.id)
                        idCmp = component.id ?: ""


                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "expressionSatisfiedsss",
                            message = expressionSatisfied.toString()
                        )

                        cmp?.let {
                            expressionSatisfied?.let {
                                cmp.updateProcessLogicDomain(
                                    cmp.processLogicDomain.value.copy(
                                        validate = expressionSatisfied.result,
                                        errorMessage = expressionSatisfied.message
                                    )
                                )
                            } ?: run {
                                cmp.updateProcessLogicDomain(
                                    cmp.processLogicDomain.value.copy(
                                        validate = false,
                                        errorMessage = null
                                    )
                                )


                            }
                            hasLogic = expressionSatisfied?.result ?: false
                        }

                    }

                    LogicType.Bind -> {
                        typeLogic = LogicType.Bind
                        val listOfBinding = it.getListOfBindingComponents()
                        Napier.log(
                            LogLevel.ASSERT,
                            tag = "listOfBinding",
                            message = listOfBinding.toString()
                        )
                        val cmp = allComponents.findComponentById(component.id)

                        if (listOfBinding.isNotEmpty()) {
                            val updatedValueDomain = updateValueDomain(
                                cmp?.values?.get(0) ?: ValueDomain(),
                                listOfBinding[0]
                            )
                            cmp?.values = listOf(updatedValueDomain)
                            cmp?.processLogicDomain?.value?.copy(calculatedValue = listOfBinding[0])
                                ?.let { it1 -> cmp.updateProcessLogicDomain(it1) }

                            hasLogic = true
                        } else {
                            val updatedValueDomain = updateValueDomain(
                                cmp?.values?.get(0) ?: ValueDomain(),
                                ""
                            )
                            cmp?.values = listOf(updatedValueDomain)
                            cmp?.processLogicDomain?.value?.copy(calculatedValue = "")
                                ?.let { it1 -> cmp.updateProcessLogicDomain(it1) }
                        }


                    }

                    LogicType.Ticket_Auto_Fill -> {
                        it.ticketAutoFillLogicDomain?.options?.let { options ->
                            for (option in options) {
                                option.phaseName?.let { phaseName ->
                                    option.property?.let { property ->
                                        viewModelScope.launch {
                                            getTicketDetailsUseCase(
                                                Pair(
                                                    ticketId,
                                                    TicketDetailRequestDomain(
                                                        listOf(
                                                            PhaseDomain(phaseName, property)
                                                        )
                                                    )
                                                )
                                            ).collect { result ->
                                                when (result.status) {
                                                    AsyncStatus.EMPTY -> {
                                                        // Handle empty case
                                                    }

                                                    AsyncStatus.ERROR -> {
                                                        // Handle error case
                                                    }

                                                    AsyncStatus.LOADING -> {
                                                        // Handle loading case
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

                                                                    component.updateValues(
                                                                        listOf(
                                                                            updatedValueDomain
                                                                        )
                                                                    )

                                                                    component.updateProcessLogicDomain(
                                                                        component.processLogicDomain.value.copy(
                                                                            calculatedValue = value
                                                                        )
                                                                    )


                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }.join()
                                    }


                                }
                            }

                        }
                    }

                }


            }
        }.join()
        val logicModel = ExtractLogicsModel(hasLogic, typeLogic, idCmp)



        if (hasLogic && (typeLogic == LogicType.Required || typeLogic == LogicType.Validate)) {
            validationErrorList.add(logicModel)
        } else {
            validationErrorList.clear()
        }
        return@coroutineScope validationErrorList
    }


    private fun LogicDomain.getListOfBindingComponents(): List<String> {
        val list: ArrayList<String> = arrayListOf()
        this.bind_logic?.field_options?.let {
            it.forEach { fieldOption ->
                val cmp = allComponents.findComponentByKey(fieldOption.field_key)
                cmp?.values?.get(0)?.value?.let { value ->
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

    fun evaluateLogics(components: List<ComponentDomain>, logicDomain: LogicDomain): Boolean {
        val results = checkExpression(components, logicDomain)
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


    private fun checkCalculation(components: List<ComponentDomain>, it: LogicDomain): String? {
        it.experssions?.forEach { exp ->
            exp.conditions?.forEach { cond ->

                when (cond.secondOperator?.title) {
                    OperatorType.Add -> {
                        return invokeOperation(OperatorType.Add, cond, components)

                    }

                    OperatorType.Subtract -> {
                        return invokeOperation(OperatorType.Subtract, cond, components)

                    }

                    OperatorType.Divide -> {
                        return invokeOperation(OperatorType.Divide, cond, components)

                    }

                    OperatorType.Multiply -> {
                        return invokeOperation(OperatorType.Multiply, cond, components)

                    }

                }

            }

        }
        return null
    }

    fun invokeOperation(
        operator: String,
        cond: ConditionDomain,
        components: List<ComponentDomain>
    ): String? {
        val componet1 = allComponents.findComponentByKey(cond.firstFieldKey)
        val componet2 = allComponents.findComponentByKey(cond.secondFieldKey)
        componet1?.values?.let { values1 ->
            if (values1.isNotEmpty()) {
                values1[0].let { value1 ->
                    componet2?.values?.let { values2 ->
                        if (values2.isNotEmpty())
                            values2[0].let { value2 ->
                                value1.value?.toFloatOrNull()?.let { v1 ->
                                    value2.value?.toFloatOrNull()?.let { v2 ->
                                        when (operator) {
                                            OperatorType.Add -> {
                                                return (v1 + v2).toStringOrEmptyString()
                                            }

                                            OperatorType.Subtract -> {
                                                return (v1 - v2).toStringOrEmptyString()
                                            }

                                            OperatorType.Multiply -> {
                                                return (v1 * v2).toStringOrEmptyString()

                                            }

                                            OperatorType.Divide -> {
                                                if (v2 == 0.0f)
                                                    return ""
                                                else
                                                    return (v1 / v2).toStringOrEmptyString()

                                            }

                                            else -> {
                                                return null
                                            }
                                        }

                                    }
                                }
                            }
                    }
                }
            }

        }
        return null
    }


    private fun checkExpression(
        components: List<ComponentDomain>,
        it: LogicDomain
    ): ArrayList<ArrayList<Boolean>> {
        val expressionResults: ArrayList<ArrayList<Boolean>> = arrayListOf()
        it.experssions?.forEachIndexed { i, expression ->
            expressionResults.add(arrayListOf())
            expression.conditions?.forEachIndexed { j, condition ->
                val component = allComponents.findComponentByKey(condition.firstFieldKey)


                when (condition.secondOperator?.title) {
                    OperatorType.IsFill -> {
                        component?.values?.let {
                            expressionResults[i].add(it[0].value?.isNotEmpty() == true)
                        }
                    }

                    OperatorType.IsBlank -> {
                        if (component?.values == null) {
                            expressionResults[i].add(true)
                        } else {
                            component.values?.let {
                                expressionResults[i].add(it[0].value?.isEmpty() == true)
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
                            component?.values?.get(0)?.let {
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
                                    component.values?.get(0)?.let {
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
                                        if (it.isSelected && !(it.value.equals(
                                                condition.value
                                            ))
                                        )
                                            expressionResults[i].add(true)
                                    }
                                } else {
                                    component.values?.get(0)?.let {
                                        if (!it.value.equals(condition.value))
                                            expressionResults[i].add(true)
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
                                        if (it.isSelected && it.value?.startsWith(
                                                condition.value.toString()
                                            ) == true
                                        )
                                            expressionResults[i].add(true)
                                    }
                                } else {
                                    component.values?.let {
                                        if (it.isNotEmpty()) {
                                            if (it.get(0).value?.startsWith(
                                                    condition.value.toString()
                                                ) == true
                                            )
                                                expressionResults[i].add(true)
                                        }
                                    }


                                    expressionResults[i].add(
                                        component.values?.get(0)?.value?.startsWith(
                                            condition.value.toString()
                                        ) == true
                                    )
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
                                    component.values?.get(0)?.let { valueDomain ->
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
                                    component.values?.get(0)?.let { valueDomain ->
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
                                    component.values?.get(0)?.let { valueDomain ->
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
                                    component.values?.get(0)?.let { valueDomain ->
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
                            if (component?.values?.filter { it.value?.isNotEmpty() == true && it.isSelected }
                                    ?.map { it.value }?.intersect(condition.values)
                                    ?.isNotEmpty() == true
                            )
                                expressionResults[i].add(true)
                        }

                    }

                    OperatorType.NotContainsAny -> {
                        condition.values?.let {
                            if (component?.values?.filter { it.value?.isNotEmpty() == true && it.isSelected }
                                    ?.map { it.value }?.intersect(condition.values)
                                    ?.isEmpty() == true
                            )
                                expressionResults[i].add(true)
                        }
                    }

                    OperatorType.ContainsAll -> {
                        condition.values?.let {
                            if (component?.values?.filter { it.value?.isNotEmpty() == true && it.isSelected }
                                    ?.map { it.value }?.containsAll(condition.values) == true)
                                expressionResults[i].add(true)
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
                val value = if (condition.firstFieldKey == "now") {
                    getCurrentDateLocalDateTime().localDateTimeToMilliseconds().toString()
                } else {
                    cmp?.values?.get(0)?.value
                }
                condition.firstOperator?.let {
                    when (condition.firstOperator.title) {
                        OperatorType.Equals -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue == currentValue,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue == currentValue,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue == currentValue,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue == currentValue,
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

                                    OperatorType.GreaterThan -> {

                                    }

                                    else -> {}
                                }
                            } ?: run {
                                val cmpValue =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.get(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()
                                    } else {
                                        componentDomain.values?.get(0)?.value?.toDouble()
                                    }
                                expressionResults[i].add(
                                    ValidateLogicResult(
                                        (cmpValue?.toDouble()
                                            ?: 0.0) == (value?.toDouble() ?: 0.0),
                                        StringDesc.ResourceFormatted(
                                            MR.strings.Equal,
                                            (value).toString()
                                        )
                                    )
                                )

                            }
                        }

                        OperatorType.NotEquals -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue != currentValue,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue != currentValue,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue != currentValue,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue != currentValue,
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
                                val cmpValue =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.get(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()
                                    } else {
                                        componentDomain.values?.get(0)?.value?.toDouble()
                                    }
                                expressionResults[i].add(
                                    ValidateLogicResult(
                                        (cmpValue?.toDouble()
                                            ?: 0.0) != (value?.toDouble() ?: 0.0),
                                        StringDesc.ResourceFormatted(
                                            MR.strings.NotEqual,
                                            (value).toString()
                                        )
                                    )
                                )

                            }
                        }

                        OperatorType.GreaterThan -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue > currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.GreaterThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue > currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.GreaterThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue > currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.GreaterThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue > currentValue,
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.GreaterThan,
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
                                val cmpValue =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.get(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()
                                    } else {
                                        componentDomain.values?.get(0)?.value?.toDouble()
                                    }
                                if (componentDomain.type == FormViewerTypes.Datetime) {
                                    expressionResults[i].add(
                                        ValidateLogicResult(
                                            (cmpValue?.toDouble()
                                                ?: 0.0) < (value?.toDouble() ?: 0.0),
                                            StringDesc.ResourceFormatted(
                                                MR.strings.GreaterThan,
                                                (value).toString()
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue > currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.GreaterOrEqualTo,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue > currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.GreaterOrEqualTo,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue > currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.GreaterOrEqualTo,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue > currentValue,
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.GreaterOrEqualTo,
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
                                val cmpValue =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.get(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()
                                    } else {
                                        componentDomain.values?.get(0)?.value?.toDouble()
                                    }
                                expressionResults[i].add(
                                    ValidateLogicResult(
                                        (cmpValue?.toDouble()
                                            ?: 0.0) >= (value?.toDouble() ?: 0.0),
                                        StringDesc.ResourceFormatted(
                                            MR.strings.GreaterOrEqualTo,
                                            (value).toString()
                                        )
                                    )
                                )
                            }

                        }

                        OperatorType.LessThan -> {
                            condition.secondOperator?.let {
                                when (condition.secondOperator?.title) {
                                    OperatorType.Add -> {
                                        value?.toFloatOrNull()?.let { fieldValue ->
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue < currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.LessThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue < currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.LessThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue < currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.LessThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue < currentValue,
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.LessThan,
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
                                val cmpValue =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.get(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()
                                    } else {
                                        componentDomain.values?.get(0)?.value?.toDouble()
                                    }
                                expressionResults[i].add(
                                    ValidateLogicResult(
                                        (cmpValue?.toDouble()
                                            ?: 0.0) < (value?.toDouble() ?: 0.0),
                                        StringDesc.ResourceFormatted(
                                            MR.strings.LessThan,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue + conditionValue <= currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.LessThanOrEqualTo,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue - conditionValue <= currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.LessThanOrEqualTo,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            expressionResults[i].add(
                                                                ValidateLogicResult(
                                                                    fieldValue * conditionValue <= currentValue,
                                                                    StringDesc.ResourceFormatted(
                                                                        MR.strings.LessThanOrEqualTo,
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
                                            condition.values?.get(0)?.toFloatOrNull()
                                                ?.let { conditionValue ->
                                                    componentDomain.values?.get(0)?.value?.toFloatOrNull()
                                                        ?.let { currentValue ->
                                                            if (conditionValue != 0.0f)
                                                                expressionResults[i].add(
                                                                    ValidateLogicResult(
                                                                        fieldValue / conditionValue <= currentValue,
                                                                        StringDesc.ResourceFormatted(
                                                                            MR.strings.LessThanOrEqualTo,
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
                                val cmpValue =
                                    if (componentDomain.type == FormViewerTypes.Datetime) {
                                        componentDomain.values?.get(0)?.value?.parseLocalDateTime()
                                            ?.localDateTimeToMilliseconds()
                                    } else {
                                        componentDomain.values?.get(0)?.value?.toDouble()
                                    }
                                expressionResults[i].add(
                                    ValidateLogicResult(
                                        (cmpValue?.toDouble()
                                            ?: 0.0) <= (value?.toDouble() ?: 0.0),
                                        StringDesc.ResourceFormatted(
                                            MR.strings.LessThanOrEqualTo,
                                            (value).toString()
                                        )
                                    )
                                )
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
        // Reduce each inner list with AND operation
        if (this[0].isNotEmpty()) {
            val innerResults = this.map { innerList ->
                innerList.reduce { acc, item ->
                    ValidateLogicResult(
                        result = acc.result && item.result,
                        message = if (acc.result && item.result) acc.message else item.message // Choose the message of the first false result or keep the true one
                    )
                }
            }

            // Reduce the results of inner lists with OR operation
            val finalResult = innerResults.reduce { acc, item ->
                ValidateLogicResult(
                    result = acc.result || item.result,
                    message = if (acc.result) acc.message else item.message // Choose the message of the first true result
                )
            }

            return finalResult
        } else {
            return null
        }
    }

    fun ComponentDomain.isSelectableValue() =
        this.type == FormViewerTypes.Checklist || this.type == FormViewerTypes.Radio || this.type == FormViewerTypes.Select || this.type == FormViewerTypes.Multi


    fun ticketId(ticketId: String) {
        this.ticketId = ticketId
    }

    private fun ComponentDomain?.clearValues() {
        if (this?.isSelectableValue() == true)
            this.values?.forEach { it.isSelected = false }
        else
            this?.values = null
    }
}



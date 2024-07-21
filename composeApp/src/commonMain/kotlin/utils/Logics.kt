package utils

import data.network.response.task.logic.LogicDomain
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.ResourceFormattedStringDesc
import dev.icerock.moko.resources.desc.StringDesc
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.ValueDomain
import domain.models.initialForm.logic.ConditionDomain
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.MR

class LogicCalculation (private val allComponents: List<ComponentDomain>) {


    fun extractLogics(components: List<ComponentDomain>, component: ComponentDomain): Boolean {

        var hasLogic = false
        component.logics?.forEach { it ->

            when (it.logicType) {
                LogicType.Hide -> {
                    val expressionSatisfied = evaluateLogics(components, it)
                    val cmp = allComponents.findComponentById(component.id)
                    Napier.log(LogLevel.ASSERT,"Hide", message = cmp.toString())

                    cmp?.processLogicDomain?.shouldHide =
                        expressionSatisfied
                    if (expressionSatisfied)
                        hasLogic = true

                }

                LogicType.Required -> {
                    val expressionSatisfied = evaluateLogics(components, it)
                    val cmp = allComponents.findComponentById(component.id)
                   cmp?.processLogicDomain?.required =
                        expressionSatisfied
                    hasLogic = true
                }

                LogicType.Disable -> {
                    val expressionSatisfied = evaluateLogics(components, it)
                    val cmp = allComponents.findComponentById(component.id)
                    cmp?.processLogicDomain?.disabled =
                        expressionSatisfied
                    hasLogic = true
                }

                LogicType.ReadOnly -> {
                    val expressionSatisfied = evaluateLogics(components, it)
                    val cmp = allComponents.findComponentById(component.id)
                    cmp?.processLogicDomain?.readOnly =
                        expressionSatisfied
                    hasLogic = true
                }


                LogicType.Calculate -> {
                    val result = checkCalculation(components, it)
                    val cmp = allComponents.findComponentById(component.id)
                    result?.let { res ->
                        val updatedValueDomain = updateValueDomain(
                            cmp?.values?.get(0) ?: ValueDomain(),
                            result
                        )
                        cmp?.values = listOf(updatedValueDomain)
                        cmp?.processLogicDomain?.calculatedValue = result
                        hasLogic = true

                    } ?: run {
                        val updatedValueDomain = updateValueDomain(
                            cmp?.values?.get(0) ?: ValueDomain(),
                            ""
                        )
                        cmp?.values = listOf(updatedValueDomain)
                        cmp?.processLogicDomain?.calculatedValue = ""
                    }

                }

                LogicType.Validate -> {
                    val expressionSatisfied = evaluateValidateLogics(components, component, it)
                    val cmp = allComponents.findComponentById(component.id)
                    Napier.log(
                        LogLevel.ASSERT,
                        tag = "expressionSatisfiedsss",
                        message = expressionSatisfied.toString()
                    )

                    cmp?.let {
                        expressionSatisfied?.let {
                            cmp.processLogicDomain.validate =
                                expressionSatisfied.result
                            cmp.processLogicDomain.errorMessage = expressionSatisfied.message
                        } ?: run {
                            cmp.processLogicDomain.validate =
                                false
                        }
                        hasLogic = true
                    }

                }

                LogicType.Bind ->{
                    val listOfBinding = it.getListOfBindingComponents()
                    Napier.log(
                        LogLevel.ASSERT,
                        tag = "listOfBinding",
                        message = listOfBinding.toString()
                    )
                    val cmp = allComponents.findComponentById(component.id)

                    if(listOfBinding.isNotEmpty()){
                        val updatedValueDomain = updateValueDomain(
                            cmp?.values?.get(0) ?: ValueDomain(),
                            listOfBinding[0]
                        )
                        cmp?.values = listOf(updatedValueDomain)
                        cmp?.processLogicDomain?.calculatedValue = listOfBinding[0]

                        hasLogic = true
                    }else{
                        val updatedValueDomain = updateValueDomain(
                            cmp?.values?.get(0) ?: ValueDomain(),
                            ""
                        )
                        cmp?.values = listOf(updatedValueDomain)
                        cmp?.processLogicDomain?.calculatedValue = ""
                    }



                }

            }
        }
        return hasLogic
    }

    private fun LogicDomain.getListOfBindingComponents() : List<String> {
        val list : ArrayList<String> =  arrayListOf()
        this.bind_logic?.field_options?.let {
            it.forEach { fieldOption->
                val cmp = allComponents.findComponentById(fieldOption.field_key)
                cmp?.values?.get(0)?.value?.let{ value->
                    if(value.isNotEmpty())
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
        val componet1 = allComponents.findComponentById(cond.firstFieldKey)
        val componet2 = allComponents.findComponentById(cond.secondFieldKey)
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
                val component = allComponents. findComponentById(condition.firstFieldKey)


                when (condition.secondOperator?.title) {
                    OperatorType.IsFill -> {
                        component?.values?.let {
                            expressionResults[i].add(it[0].value?.isNotEmpty() == true)
                        }
                    }

                    OperatorType.IsBlank -> {
                        if (component?.values == null)
                            expressionResults[i].add(true)
                        component?.values?.let {
                            expressionResults[i].add(it[0].value?.isEmpty() == true)
                        }
                    }

                    OperatorType.Equals -> {
                        if (component?.values?.get(0)?.value == condition.value.toString())
                            expressionResults[i].add(true)
                    }

                    OperatorType.NotEquals -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                if (component.values?.get(0)?.value != condition.value.toString())
                                    expressionResults[i].add(true)
                            }
                        }
                    }

                    OperatorType.Contains -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                expressionResults[i].add(
                                    component.values?.get(0)?.value?.contains(
                                        condition.value.toString()
                                    ) == true
                                )
                            }
                        }

                    }

                    OperatorType.StartWith -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                expressionResults[i].add(
                                    component.values?.get(0)?.value?.startsWith(
                                        condition.value.toString()
                                    ) == true
                                )
                            }
                        }

                    }

                    OperatorType.GreaterThan -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                component.values?.get(0)?.value?.let { value ->
                                    if (value.isNotEmpty())
                                        value.toFloatOrNull()?.let { intValue ->
                                            condition.value?.let { conditionValue ->
                                                if (conditionValue.isNotEmpty()) {
                                                    conditionValue.toFloatOrNull()
                                                        ?.let { intConditionValue ->
                                                            expressionResults[i].add(
                                                                intValue > intConditionValue
                                                            )
                                                        }
                                                }

                                            }

                                        }
                                }

                            }
                        }

                    }

                    OperatorType.GreaterThanOrEqualsTo -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                component.values?.get(0)?.value?.let { value ->
                                    if (value.isNotEmpty())
                                        value.toFloatOrNull()?.let { intValue ->
                                            condition.value?.let { conditionValue ->
                                                if (conditionValue.isNotEmpty()) {
                                                    conditionValue.toFloatOrNull()
                                                        ?.let { intConditionValue ->
                                                            expressionResults[i].add(
                                                                intValue >= intConditionValue
                                                            )
                                                        }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }

                    OperatorType.LessThan -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                component.values?.get(0)?.value?.let { value ->
                                    if (value.isNotEmpty())
                                        value.toFloatOrNull()?.let { intValue ->
                                            Napier.log(
                                                LogLevel.ASSERT,
                                                tag = "evaluateLogics",
                                                message = intValue.toString()
                                            )

                                            condition.value?.let { conditionValue ->
                                                Napier.log(
                                                    LogLevel.ASSERT,
                                                    tag = "evaluateLogics",
                                                    message = conditionValue
                                                )

                                                if (conditionValue.isNotEmpty()) {
                                                    conditionValue.toFloatOrNull()
                                                        ?.let { intConditionValue ->
                                                            expressionResults[i].add(
                                                                intValue < intConditionValue
                                                            )
                                                        }
                                                }

                                            }

                                        }
                                }

                            }
                        }
                    }

                    OperatorType.LessThanOrEqualsTo -> {
                        component?.values?.let {
                            if (it.isNotEmpty()) {
                                component.values?.get(0)?.value?.let { value ->
                                    if (value.isNotEmpty())
                                        value.toFloatOrNull()?.let { intValue ->
                                            condition.value?.let { conditionValue ->
                                                if (conditionValue.isNotEmpty()) {
                                                    conditionValue.toFloatOrNull()
                                                        ?.let { intConditionValue ->
                                                            expressionResults[i].add(
                                                                intValue <= intConditionValue
                                                            )
                                                        }
                                                }

                                            }

                                        }
                                }

                            }
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



    private fun List<ComponentDomain>.findComponentById(firstFieldKey: String?): ComponentDomain? {
        for (component in this) {
            if (component.id == firstFieldKey) {
                return component
            }
            component.components?.findComponentById(firstFieldKey)?.let { return it }
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
                val cmp = allComponents.findComponentById(condition.firstFieldKey)
                val value = cmp?.values?.get(0)?.value
                condition.firstOperator?.let {
                    when (condition.firstOperator.title) {
                        OperatorType.Equals -> {
                            when (condition.secondOperator?.title) {
                                OperatorType.Add -> {
                                    value?.toFloatOrNull()?.let { fieldValue ->
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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

                                else -> {}
                            }
                        }

                        OperatorType.NotEquals -> {
                            when (condition.secondOperator?.title) {
                                OperatorType.Add -> {
                                    value?.toFloatOrNull()?.let { fieldValue ->
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                        }

                        OperatorType.GreaterThan -> {
                            when (condition.secondOperator?.title) {
                                OperatorType.Add -> {
                                    value?.toFloatOrNull()?.let { fieldValue ->
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                        }

                        OperatorType.GreaterThanOrEqualsTo -> {
                            when (condition.secondOperator?.title) {
                                OperatorType.Add -> {
                                    value?.toFloatOrNull()?.let { fieldValue ->
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                        }

                        OperatorType.LessThan -> {
                            when (condition.secondOperator?.title) {
                                OperatorType.Add -> {
                                    value?.toFloatOrNull()?.let { fieldValue ->
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                        }

                        OperatorType.LessThanOrEqualsTo -> {
                            when (condition.secondOperator?.title) {
                                OperatorType.Add -> {
                                    value?.toFloatOrNull()?.let { fieldValue ->
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                                        condition.value?.toFloatOrNull()?.let { conditionValue ->
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
                        }


                        else -> {}
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
}


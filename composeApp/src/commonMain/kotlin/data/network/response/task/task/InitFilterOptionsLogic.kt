package data.network.response.task.task

import data.network.response.task.logic.LogicCondition
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable



@Serializable
data class InitFilterOptionsLogic(
    @SerialName("conditions")
    val conditions : List<InitLogicCondition>?=null,
    @SerialName("filteredOptions")
    val filteredOptions : List<String>?=null,
    @SerialName("multiSelectValues")
    val multiSelectValues : List<String>?=null,
    @SerialName("selectedKeyValues")
    val selectedKeyValues : List<String>?=null)
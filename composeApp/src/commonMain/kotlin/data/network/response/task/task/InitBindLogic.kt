package data.network.response.task.task

import data.network.response.task.logic.FieldOption
import kotlinx.serialization.Serializable



@Serializable
data class InitBindLogic(
    val field_options : List<InitFieldOption>
)
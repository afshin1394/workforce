package data.network.response.task.logic

import kotlinx.serialization.Serializable

@Serializable
data class BindLogic(
    val field_options : List<FieldOption>
)

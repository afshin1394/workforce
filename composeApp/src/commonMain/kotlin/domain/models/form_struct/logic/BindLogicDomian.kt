package data.network.response.task.logic

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable
@Serializable
data class BindLogicDomian(
    val field_options : List<FieldOptionDomain>
) 

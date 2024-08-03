package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Value(val label : String?= null,val value : String?= null, var isSelected: Boolean = false)

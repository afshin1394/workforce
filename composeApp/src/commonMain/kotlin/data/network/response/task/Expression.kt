package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Expression(val conditions : List<Condition>?= null)

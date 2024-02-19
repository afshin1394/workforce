package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Operator(val title : String?= null,val symbol : String?= null)

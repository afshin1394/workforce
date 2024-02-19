package data.network.response.task

import kotlinx.serialization.Serializable

@Serializable
data class Layout(val row : String?= null,val columns : String?= null)

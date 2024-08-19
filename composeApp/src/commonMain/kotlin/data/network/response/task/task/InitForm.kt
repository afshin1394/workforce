package data.network.response.task.task

import data.network.response.task.Value
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InitForm(
    @SerialName("key")
    private val key : String,
//    @SerialName("values")
//    private val value : Any,
    )

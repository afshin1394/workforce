
package data.network.response.task.task

import data.network.response.task.Value
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import utils.AnySerializer
@Serializable
data class InitForm(
    @SerialName("key")
    val key : String,
    @SerialName("values")
    val value : String,
    )


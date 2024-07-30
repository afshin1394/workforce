package data.network.response.task.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    @SerialName("id")
    val id : Long?,
    @SerialName("title")
    val title : String?,
    @SerialName("process_id")
    val process_id : Int?,
    @SerialName("task")
    val task : Int?,
    @SerialName("kind")
    val kind : String?,
    @SerialName("form")
    val form : Form?,
    @SerialName("tag")
    val tag : Long?,
    @SerialName("form_id")
    val form_id : Int?,
    )

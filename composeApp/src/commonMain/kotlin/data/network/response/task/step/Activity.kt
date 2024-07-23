package data.network.response.task.step

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Activity(
    @SerialName("id")
    val id : Int,
    @SerialName("title")
    val title : Int,
    @SerialName("process_id")
    val process_id : Int,
    @SerialName("task")
    val task : Int,
    @SerialName("kind")
    val kind : Int,
   // @SerialName("form")
   // val form : Form,
    @SerialName("tag")
    val tag : Int,
    @SerialName("form_id")
    val form_id : Int,
    )

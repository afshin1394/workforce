package data.network.response.task

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Validate (
    @SerialName("id")
    val id : String?=null,
    @SerialName("key")
    val key : String?=null,
    @SerialName("hide")
    val hide : String?=null,
    @SerialName("layout")
    val layout : Layout?=null,
    @SerialName("subtype")
    val subtype : String? = null,
    @SerialName("required")
    val required : Boolean?= null,
)

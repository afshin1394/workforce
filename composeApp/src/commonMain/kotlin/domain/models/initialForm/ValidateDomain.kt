package domain.models.initialForm

import data.network.response.task.Layout
import kotlinx.serialization.Serializable

@Serializable
data class ValidateDomain(
    val id : String?=null,
    val key : String?=null,
    val hide : String?=null,
    val layout : Layout?=null,
    val subtype : String? = null,
    val required : Boolean?= null,
)

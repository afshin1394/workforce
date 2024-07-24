package domain.models.steps

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ActivityDomain(
    val id : Int,
    val title : String,
    val process_id : Int,
    val task : Int,
    val kind : String,
    val form : FormDomain,
    val tag : Int,
    val form_id : Int,
    )

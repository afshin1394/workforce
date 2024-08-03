package domain.models.steps

import domain.models.PhotoDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class ActivityDomain(
    val id : Long,
    val title : String,
    val process_id : Int,
    val task : Int,
    val kind : String,
    val form : FormDomain,
    val photoDomainList : List<PhotoDomain>,
    val tag : Long,
    val form_id : Int,
    )

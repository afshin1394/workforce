package domain.models.form_struct

import dev.icerock.moko.parcelize.Parcelable
import kotlinx.serialization.Serializable
data class LayoutDomain(val row : String?= null,val columns : String?= null)

package domain.models.form_struct

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import dev.icerock.moko.parcelize.Parcelable
import kotlinx.serialization.Serializable
@Serializable
data class LayoutDomain(val row : String?= null,val columns : String?= null) 

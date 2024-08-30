package domain.models.form_struct

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import kotlinx.serialization.Serializable
@Serializable
data class ConditionalDomain(val string : String?= null) 

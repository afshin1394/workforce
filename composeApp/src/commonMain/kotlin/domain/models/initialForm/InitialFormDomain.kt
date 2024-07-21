package domain.models.initialForm

import kotlinx.serialization.Serializable


data class InitialFormDomain(
     val ticket_number : String,
     val structure : InitialFormStructureDomain
){
     override fun toString(): String {
          return "InitialFormDomain(wi_id=$ticket_number, structure=$structure)"
     }
}

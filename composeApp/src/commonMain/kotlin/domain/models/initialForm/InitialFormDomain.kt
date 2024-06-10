package domain.models.initialForm

import kotlinx.serialization.Serializable


data class InitialFormDomain(
     val wi_id : Long,
     val structure : InitialFormStructureDomain
){
     override fun toString(): String {
          return "InitialFormDomain(wi_id=$wi_id, structure=$structure)"
     }
}

package domain.models.form_struct


data class InitialFormDomain(
     val ticket_number : String,
     val structure : FormStructDomain
){
     override fun toString(): String {
          return "InitialFormDomain(wi_id=$ticket_number, structure=$structure)"
     }
}

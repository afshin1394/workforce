package domain.models.form_struct

import domain.models.task.InstanceTicketsBasicInformationValuesDomain


data class InitialFormDomain(
     val ticket_number : String,
     val initForms : List<InstanceTicketsBasicInformationValuesDomain>
){
     override fun toString(): String {
          return "InitialFormDomain(wi_id=$ticket_number, structure=$initForms)"
     }
}

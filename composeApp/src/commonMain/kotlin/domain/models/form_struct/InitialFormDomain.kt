package domain.models.form_struct

import data.network.response.task.task.InitStructure
import domain.models.task.InstanceTicketsBasicInformationValuesDomain


data class InitialFormDomain(
     val ticket_number: String,
     val initForms:List<InstanceTicketsBasicInformationValuesDomain>,
     val initStructure: InitFormStructDomain
){
     override fun toString(): String {
          return "InitialFormDomain(wi_id=$ticket_number, structure=$initStructure)"
     }
}

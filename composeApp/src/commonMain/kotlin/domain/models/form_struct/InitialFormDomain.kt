package domain.models.form_struct

import data.network.response.task.task.InitForm
import domain.models.task.InitFormDomain


data class InitialFormDomain(
     val ticket_number : String,
     val initForms : List<InitFormDomain>
){
     override fun toString(): String {
          return "InitialFormDomain(wi_id=$ticket_number, structure=$initForms)"
     }
}

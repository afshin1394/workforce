package presentation.model

import domain.models.form_struct.FormStructDomain

data class StepModel (val activityID : Int,val activityTitle : String,var isActive : Boolean = false, val form_structure : FormStructDomain
)
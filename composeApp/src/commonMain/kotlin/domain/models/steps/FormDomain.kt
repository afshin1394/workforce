package domain.models.steps

import data.network.response.task.FormStruct
import domain.models.form_struct.FormStructDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
data class FormDomain(
    val form_structure : FormStructDomain

)




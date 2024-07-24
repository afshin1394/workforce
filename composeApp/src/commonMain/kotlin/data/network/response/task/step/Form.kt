package data.network.response.task.step

import data.network.response.task.FormStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class Form(
    @SerialName("form_structure")
    val form_structure : FormStruct

)




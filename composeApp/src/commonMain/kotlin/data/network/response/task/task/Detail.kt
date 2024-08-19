package data.network.response.task.task

import data.network.response.task.FormStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Detail(
    @SerialName("basic_info")
    val basic_info: BasicInfo,
    @SerialName("init_form")
    val initial_form: List<InitForm>? = null,
){
    override fun toString(): String {
        return "Detail(basic_info=$basic_info)"
    }
}

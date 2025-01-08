package data.network.response.task.step

import data.network.response.task.FormStruct
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StepDetail(
    @SerialName("activity_id")
    val activity_id : Long,
    @SerialName("activity_title")
    val activity_title : String,
    @SerialName("activity_process_id")
    val activity_process_id : Long,
    @SerialName("activity_task_group")
    val activity_task_group : String?,
    @SerialName("activity_kind")
    val activity_kind : String,
    @SerialName("activity_form")
    val activity_form : Long,
    @SerialName("workflow_activity_tags_id")
    val workflow_activity_tags_id : Long,
    @SerialName("form_name")
    val form_name : String,
    @SerialName("form_structure")
    val form_structure : FormStruct
){
    override fun toString(): String {
        return "Detail(activity_title=$activity_title )"
    }
}

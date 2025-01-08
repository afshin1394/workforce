package domain.models.steps

import data.network.response.task.FormStruct
import domain.models.form_struct.FormStructDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class StepDetailDomain(

    val activity_id : Long,
    val activity_title : String,
    val activity_process_id : Long,
    val activity_task_group : String,
    val activity_kind : String,
    val activity_form : Long,
    val workflow_activity_tags_id : Long,
    val form_name : String,
    val form_structure : FormStructDomain

)

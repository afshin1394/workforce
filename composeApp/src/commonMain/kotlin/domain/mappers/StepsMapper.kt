package domain.mappers

import data.network.response.task.Component
import data.network.response.task.FormStruct
import data.network.response.task.Value
import data.network.response.task.step.Activity
import data.network.response.task.step.Form
import data.network.response.task.step.TaskStepResponse
import database.entity.SendStepsEntity
import database.entity.StepsEntity
import domain.mappers.toComponentDomain
import domain.mappers.toConditionalDomain
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.FormStructDomain
import domain.models.steps.ActivityDomain
import domain.models.steps.FormDomain
import domain.models.steps.StepDetailDomain
import kotlinx.serialization.json.Json
import utils.FormViewerTypes

fun TaskStepResponse.toStepDetailsDomain(): List<StepDetailDomain> =
    this.detail.map {
        StepDetailDomain(

            activity_id = it.activity_id,
            activity_title = it.activity_title,
            activity_process_id = it.activity_process_id,
            activity_task_group = it.activity_task_group?:"",
            activity_kind = it.activity_kind,
            activity_form = it.activity_form,
            workflow_activity_tags_id = it.workflow_activity_tags_id,
            form_name = it.form_name,
            form_structure = it.form_structure.toFormStructDomain(),


        )
    }


fun List<Activity>.toActivityDomains(): List<ActivityDomain?> {
    return this.map {
        it.form?.toFormDomain()?.let { it1 ->
            ActivityDomain(
                it.id ?: -1,
                it.title ?: "",
                it.process_id ?: -1,
                it.task ?: 1,
                it.kind ?: "",
                it1,
                it.tag ?: 1,
                it.form_id ?: 1,
                edited = false
            )
        }
    }
}


fun Form.toFormDomain() = FormDomain(this.form_structure.toFormStructDomain())


fun FormStruct.toFormStructDomain() = FormStructDomain(
    this.id,
    this.hide,
    this.type,
    this.components?.toComponentDomain(),
    this.conditional?.toConditionalDomain(),
    this.schemaVersion
)

fun Component.isSelectable() =
    this.type == FormViewerTypes.Checklist || this.type == FormViewerTypes.Radio || this.type == FormViewerTypes.Select || this.type == FormViewerTypes.Multi


fun TaskStepResponse.toStepDetailsEntity(ticketNumber: String): List<StepsEntity> {

    return if (this.detail.isNotEmpty()) {
        this.detail.map { stepDetail ->



            val updatedFormStruct = stepDetail.form_structure.copy(
                components = stepDetail.form_structure.components?.map { component ->
                    if (component.isSelectable() && component.defaultValue != null) {
                        val defaultValueString = when (component.defaultValue) {
                            is String -> component.defaultValue
                            else -> component.defaultValue.toString()
                        }

                        val updatedValues = component.values?.map { value ->
                            if (value.value == defaultValueString) {
                                value.copy(isSelected = true)
                            } else {
                                value
                            }
                        } ?: emptyList()

                        component.copy(values = updatedValues)

                    } else if (component.defaultValue != null) {
                        val existingValues = component.values ?: emptyList()
                        val defaultValueString = when (component.defaultValue) {
                            is String -> component.defaultValue
                            else -> component.defaultValue.toString()
                        }
                        component.copy(
                            values = existingValues + Value(defaultValueString)
                        )

                    } else {
                        component
                    }
                }
            )


            StepsEntity(
                pk = 0,
                title = stepDetail.activity_title,
                tag = stepDetail.workflow_activity_tags_id,
                ticketNumber = ticketNumber,

                wi = 0,
                activityId = stepDetail.activity_id,

                formStructure = Json.encodeToString(
                    FormStruct.serializer(),
                    updatedFormStruct
                ),
                edited = false,
                isSent = false
            )
        }
    } else {
        emptyList()
    }
}






fun StepsEntity.toActivityDomain(): ActivityDomain {
    val json = Json { 
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Add debugging and error handling for JSON parsing
    val formStructDomain = try {
        if (this.formStructure.isBlank()) {
            // Return default empty FormStructDomain if JSON is blank
            FormStructDomain()
        } else {
            // Check if the data looks like toString() output instead of JSON
            if (this.formStructure.startsWith("FormStruct(") || 
                this.formStructure.contains("ConditionalDomain(") ||
                !this.formStructure.trim().startsWith("{")) {
                
                println("Detected malformed JSON (toString format) for StepsEntity ${this.pk}")
                println("Corrupted data: ${this.formStructure.take(100)}...")
                
                // Return a minimal valid FormStructDomain for corrupted data
                FormStructDomain(
                    id = "corrupted_${this.pk}",
                    type = "form",
                    components = emptyList(),
                    schemaVersion = 11
                )
            } else {
                // Try to parse as proper JSON
                json.decodeFromString<FormStruct>(this.formStructure).toFormStructDomain()
            }
        }
    } catch (e: Exception) {
        // Log the error and return default FormStructDomain
        println("JSON parsing failed for StepsEntity ${this.pk}: ${e.message}")
        println("Raw content length: ${this.formStructure.length}")
        
        // Return a default FormStructDomain to prevent crashes
        FormStructDomain(
            id = "error_${this.pk}",
            type = "form",
            components = emptyList(),
            schemaVersion = 11
        )
    }

    return ActivityDomain(
        id = this.activityId,
        title = this.title,
        process_id = this.activityId.toInt(),
        task = 0,
        kind = "",
        form = FormDomain(form_structure = formStructDomain),
        form_id = this.pk.toInt(),
        tag = this.tag,
        edited = this.edited
    )
}

fun List<StepsEntity>.toActivityDomainList(): List<ActivityDomain> {
    return this.map {
        it.toActivityDomain()
    }
}

//sendStep

fun StepsEntity.toSendStepEntity(): SendStepsEntity {
    return SendStepsEntity(
        this.pk,
        this.ticketNumber,
        this.wi,
        this.title,
        this.tag,
        this.activityId,
        "",
        "",
        this.edited
    )
}

//
fun List<StepsEntity>.toSendStepEntity(): List<SendStepsEntity> {
    return map {
        it.toSendStepEntity()
    }
}
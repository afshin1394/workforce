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
    this.stepDetails.map {
        StepDetailDomain(
            init_wi = it.init_wi,
            acitivities = it.acitivities.toActivityDomains()
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
    return if (this.stepDetails.isNotEmpty()) {
        this.stepDetails[0].let { stepDetail ->
            stepDetail.acitivities.map { activity ->
                val updatedFormStruct = activity.form?.form_structure?.copy(
                    components = activity.form.form_structure.components?.map { component ->
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

                            component.copy(
                                values = updatedValues
                            )
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
                    title = activity.title ?: "",
                    tag = activity.tag ?: -1,
                    ticketNumber = ticketNumber,
                    wi = stepDetail.init_wi,
                    activityId = activity.id ?: -1,
                    formStructure = Json.encodeToString(
                        FormStruct.serializer(),
                        updatedFormStruct ?: activity.form!!.form_structure
                    ),
                    edited = false,
                    isSent = false
                )
            }
        }
    } else {
        emptyList()
    }
}


fun StepsEntity.toActivityDomain(): ActivityDomain {
    val json = Json { ignoreUnknownKeys = true }

    return ActivityDomain(
        id = this.activityId,
        title = this.title,
        process_id = this.activityId.toInt(),
        task = 0,
        kind = "",
        form = FormDomain(
            form_structure = json.decodeFromString<FormStruct>(this.formStructure)
                .toFormStructDomain()
        ),
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
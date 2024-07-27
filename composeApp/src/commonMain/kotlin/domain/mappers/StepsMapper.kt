import data.network.response.task.FormStruct
import data.network.response.task.step.Activity
import data.network.response.task.step.Form
import data.network.response.task.step.TaskStepResponse
import database.entity.StepsEntity
import domain.mappers.toComponentDomain
import domain.mappers.toConditionalDomain
import domain.mappers.toInitialFormDomain
import domain.models.form_struct.FormStructDomain
import domain.models.steps.ActivityDomain
import domain.models.steps.FormDomain
import domain.models.steps.StepDetailDomain
import kotlinx.serialization.json.Json

fun TaskStepResponse.toStepDetailsDomain(): List<StepDetailDomain> =
    this.stepDetails.map {
        StepDetailDomain(
            init_wi = it.init_wi,
            acitivities = it.acitivities.toActivityDomains()
        )
    }


fun List<Activity>.toActivityDomains() =
    this.map {
        ActivityDomain(
            it.id,
            it.title,
            it.process_id,
            it.task,
            it.kind,
            it.form.toFormDomain(),
            it.tag,
            it.form_id
        )
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


//

fun TaskStepResponse.toStepDetailsEntity(ticketNumber: String): List<StepsEntity> {

    return if (this.stepDetails.isNotEmpty()) {
        this.stepDetails[0].let { stepDetail ->
            stepDetail.acitivities.map { activity ->
                StepsEntity(
                    pk = 0,
                    ticketNumber = ticketNumber,
                    wi = stepDetail.init_wi,
                    activityId = activity.id,
                    formStructure = Json.encodeToString(
                        FormStruct.serializer(),
                        activity.form.form_structure
                    ),
                    edited = false
                )
            }
        }
    } else {
        emptyList()
    }


}
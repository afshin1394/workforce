import data.network.response.task.FormStruct
import data.network.response.task.step.Activity
import data.network.response.task.step.Form
import data.network.response.task.step.TaskStepResponse
import database.entity.SendStepsEntity
import database.entity.StepsEntity
import domain.mappers.toComponentDomain
import domain.mappers.toConditionalDomain
import domain.models.PhotoDomain
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


fun List<Activity>.toActivityDomains():List<ActivityDomain?> {
   return this.map {
       it.form?.toFormDomain()?.let { it1 ->
           ActivityDomain(
               it.id ?: -1,
               it.title ?: "",
               it.process_id ?: -1,
               it.task ?: 1,
               it.kind ?: "",
               it1,
               arrayListOf(),
               it.tag ?: 1,
               it.form_id ?: 1
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


//



fun TaskStepResponse.toStepDetailsEntity(ticketNumber: String,photoList : String): List<StepsEntity> {

    return if (this.stepDetails.isNotEmpty()) {
        this.stepDetails[0].let { stepDetail ->
            stepDetail.acitivities.map { activity ->
                StepsEntity(
                    pk = 0,
                    title = activity.title?:"",
                    tag = activity.tag?:-1,
                    ticketNumber = ticketNumber,
                    wi = stepDetail.init_wi,
                    activityId = activity.id?:-1,
                    formStructure = Json.encodeToString(
                        FormStruct.serializer(),
                        activity.form!!.form_structure
                    ),
                    edited = false,
                    photoList = photoList,
                    isSent = false
                )
            }
        }
    } else {
        emptyList()
    }


}

fun StepsEntity.toActivityDomain() : ActivityDomain{
    val json = Json { ignoreUnknownKeys = true }
    val photoDomainList = try{
        json.decodeFromString<List<PhotoDomain>>(this.photoList)

    }catch (exception:Exception){
        arrayListOf()
    }
    return ActivityDomain(id = this.activityId, title = this.title, process_id = this.activityId.toInt(), task = 0,kind="", form = FormDomain(form_structure = json.decodeFromString<FormStruct>(this.formStructure).toFormStructDomain())  , form_id = this.pk.toInt(), photoDomainList = photoDomainList, tag = this.tag)
}

fun List<StepsEntity>.toActivityDomainList(): List<ActivityDomain>{
    return  this.map {
        it.toActivityDomain()
    }
}

//sendStep

fun StepsEntity.toSendStepEntity() : SendStepsEntity {
    return SendStepsEntity(this.pk,this.ticketNumber,this.wi,this.title,this.tag,this.activityId,"","",this.edited)
}
//
fun List<StepsEntity>.toSendStepEntity() : List<SendStepsEntity>{
    return map{
        it.toSendStepEntity()
    }
}
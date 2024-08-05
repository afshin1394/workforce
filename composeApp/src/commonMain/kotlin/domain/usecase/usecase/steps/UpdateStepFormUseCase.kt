package domain.usecase.usecase.steps


import arrow.core.Tuple4
import data.network.response.task.FormStruct
import domain.mappers.toComponent
import domain.mappers.toPhotoEntityList
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.steps.ActivityDomain
import domain.repository.IPhotoRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import toActivityDomain
import toActivityDomainList
import utils.PROCEED

data class StructureActivity(
    val stepCounter: Int,
    val stepTitle: String,
    val activityDomain: ActivityDomain,
    val stepDetails: List<StepDetail>,
)

data class StepDetail(val id: Int, val name: String)
class UpdateStepFormUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iPhotoRepository: IPhotoRepository,
) : BaseUseCase<StructureActivity, Tuple4<String, String, List<ComponentDomain>, List<PhotoDomain>>>() {

    override suspend fun run(params: Tuple4<String, String,List<ComponentDomain>,List<PhotoDomain>>): StructureActivity {
        val stepList: List<ActivityDomain> =
            iStepsRepository.getStepsByTicketNumber(params.first).toActivityDomainList()

        val stepListSorted = stepList.sortedBy { it.id }
        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)

        if(params.second != PROCEED.INITIAL) {
            iStepsRepository.updateFormStructure(
                params.first,
                stepPointerDomain.activeActivity,
                formStructure = Json.encodeToString(
                    FormStruct.serializer(),
                    FormStruct(components = (params.third.toComponent()))
                ),
                photoList = Json.encodeToString(params.fourth)
            )

        }

        val stepDetails = stepListSorted.mapIndexed { int, step ->
            StepDetail(int, step.title)
        }
        var index =  stepListSorted.indexOfFirst { it.id == stepPointerDomain.activeActivity }
        if(index == -1) index = 0

        val nextIndex =
            if (params.second == PROCEED.INITIAL) {
                index
            } else if (params.second == PROCEED.NEXT) {
                if (index <= stepListSorted.size) {
                    index + 1
                }
                else {
                    index
                }
            } else if (params.second == PROCEED.PREVIOUS) {
                if (index > 0) {

                    index - 1
                }else {
                    index
                }
            } else {
                index
            }
        Napier.log(LogLevel.ASSERT,tag="nextIndex", message = nextIndex.toString())

        iStepPointerRepository.updateActiveActivity(
            ticketNumber = stepPointerDomain.ticketNumber,
            activeActivity = stepListSorted[nextIndex].id
        )
       val data =iStepsRepository.getDataByTicketNumberAndStep(
                stepPointerDomain.ticketNumber,
        stepListSorted.get(nextIndex).id
        ).toActivityDomain()
         Napier.log(LogLevel.ASSERT,tag="dataaaaaaa", message = data.toString())
        return StructureActivity(
            nextIndex,
            stepListSorted.get(nextIndex).title,
            data,
            stepDetails,
        )

    }


    private fun mapSubtypeToType(subtype: String?): String? {
        return when (subtype) {
            "datetime" -> "datetime"
            "date" -> "date"
            "time" -> "time"
            "multi" -> "multi"
            else -> null
        }

    }

}
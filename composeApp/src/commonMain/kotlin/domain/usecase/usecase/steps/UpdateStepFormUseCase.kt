package domain.usecase.usecase.steps


import arrow.core.Tuple4
import arrow.core.Tuple5
import data.network.response.task.FormStruct
import domain.mappers.toComponent
import domain.mappers.toPhotoEntityList
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import domain.models.steps.ActivityDomain
import domain.repository.IPhotoRepository
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Location
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import toActivityDomain
import toActivityDomainList
import utils.FormViewerTypes
import utils.PROCEED
import utils.toJson

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
    private val iSendStepsRepository: ISendStepsRepository,
) : BaseUseCase<StructureActivity, Tuple5<String,  String, List<ComponentDomain>, List<PhotoDomain>,Int>>() {

    override suspend fun run(params: Tuple5<String, String,List<ComponentDomain>,List<PhotoDomain>,Int>): StructureActivity {
        val dict = mutableMapOf<String, Any>()

        val stepList: List<ActivityDomain> =
            iStepsRepository.getStepsByTicketNumber(params.first).toActivityDomainList()

        val stepListSorted = stepList.sortedBy { it.id }
        val stepDetails = stepListSorted.mapIndexed { int, step ->
            StepDetail(int, step.title)
        }
        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)
        var index =  stepListSorted.indexOfFirst { it.id == stepPointerDomain.activeActivity }
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




        try {

            val location = Location.getLastLocation()
            params.third.findComponentByKey("submitted_latitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.latitude))
            params.third.findComponentByKey("submitted_longitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.longitude))
//            data.form.form_structure.components?.findComponentByKey("submitted_date")?.values =
//                arrayListOf(ValueDomain("submitted_date", location.datetime))
        }catch (e:Exception){

        }
        params.third.getKeysAndValues(dict)
        Napier.log(LogLevel.ASSERT , "keyValue ", message = dict.toJson() )
        iSendStepsRepository.updateKeyValueStructure(stepPointerDomain.ticketNumber,
            stepListSorted[params.fifth].id,dict.toJson())
        iStepPointerRepository.updateActiveActivity(
            ticketNumber = stepPointerDomain.ticketNumber,
            activeActivity = stepListSorted[nextIndex].id
        )







        val nextData = iStepsRepository.getDataByTicketNumberAndStep(
            stepPointerDomain.ticketNumber,
            stepListSorted.get(index = nextIndex).id
        ).toActivityDomain()



        //updateStepFormUseCase


        return StructureActivity(
            nextIndex,
            stepListSorted[nextIndex].title,
            nextData,
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

    fun ComponentDomain.isSelectable() =
        this.type == FormViewerTypes.Checklist || this.type == FormViewerTypes.Radio || this.type == FormViewerTypes.Select


    fun ComponentDomain.addSelectableItems(dict : MutableMap<String,Any>) {
        if (this.values?.filter { it.isSelected }?.isNotEmpty() == true) {
            dict[this.key ?: ""] = arrayListOf<String>()
            this.values?.forEach { value ->
                if (value.isSelected)
                    (dict[this.key] as ArrayList<String>).add(
                        value.value ?: ""
                    )
            }
        }
    }

    fun ComponentDomain.addSelectableItem(dict: MutableMap<String,Any>) {
        if (this.values?.get(0)?.isSelected == true)
            dict[this.key ?: ""] =
                this.values?.get(0)?.value.toString()
    }

    fun ComponentDomain.addItems(dict: MutableMap<String,Any>) {
        dict[this.key ?: ""] = arrayListOf<String>()
        this.values?.forEach { value ->
            (dict[this.key] as ArrayList<String>).add(
                value.value ?: ""
            )
        }
    }

    fun ComponentDomain.addItem(dict: MutableMap<String,Any>) {
        dict[this.key ?: ""] =
            this.values?.get(0)?.value.toString()
    }

    fun List<ComponentDomain>.getKeysAndValues(dict: MutableMap<String,Any>)  {
        this.forEach { componentDomain ->
            componentDomain.values?.let { values ->

                if (values.isNotEmpty()) {
                    if(componentDomain.isSelectable()){
                        if(values.filter { it.isSelected }.size> 1)
                            componentDomain.addSelectableItems(dict)
                        else
                            componentDomain.addSelectableItem(dict)


                    }else{
                        if(values.size>1)
                            componentDomain.addItems(dict)
                        else
                            componentDomain.addItem(dict)
                    }

                }
            }
            componentDomain.components?.getKeysAndValues(dict)
        }
    }

    private fun List<ComponentDomain>.findComponentByKey(key: String): ComponentDomain? {
        this.forEach { component ->
            if (component.key == key) {
                return component
            }

            // Recursively search in the children
            val found = component.components?.findComponentByKey(key)
            if (found != null) {
                return found
            }
        }
        return null
    }
}
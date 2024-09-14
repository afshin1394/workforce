package domain.usecase.usecase.steps


import arrow.core.Tuple4
import arrow.core.Tuple5
import data.network.response.task.FormStruct
import domain.mappers.toComponent
import domain.mappers.toPhotoDomainList
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
import utils.getCurrentDate
import utils.parsGpsDateTime
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
    private val iPhotoRepository: IPhotoRepository,
) : BaseUseCase<StructureActivity, Tuple5<String, String, List<ComponentDomain>, List<PhotoDomain>, Int>>() {

    override suspend fun run(params: Tuple5<String, String, List<ComponentDomain>, List<PhotoDomain>, Int>): StructureActivity {
        Location.start { }
        val removablesWithParent: ArrayList<ComponentDomain> = arrayListOf()
        val dict = mutableMapOf<String, Any>()
        val dictImages = mutableMapOf<String, Any>()
        Napier.log(LogLevel.ASSERT, tag = "processType", message = params.second)
        val stepList: List<ActivityDomain> =
            iStepsRepository.getStepsByTicketNumber(params.first).toActivityDomainList()

        val stepListSorted = stepList.sortedBy { it.id }
        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)

        if (params.second != PROCEED.INITIAL) {
            iStepsRepository.updateFormStructure(
                params.first,
                stepPointerDomain.activeActivity,
                formStructure = Json.encodeToString(
                    FormStruct.serializer(),
                    FormStruct(components = (params.third.toComponent()))
                ),
            )

        }

        val stepDetails = stepListSorted.mapIndexed { int, step ->
            StepDetail(int, step.title)
        }
        var index = stepListSorted.indexOfFirst { it.id == stepPointerDomain.activeActivity }
        if (index == -1) index = 0

        val nextIndex =
            if (params.second == PROCEED.INITIAL) {
                index
            } else if (params.second == PROCEED.NEXT) {
                if (index <= stepListSorted.size) {
                    index + 1
                } else {
                    index
                }
            } else if (params.second == PROCEED.PREVIOUS) {
                if (index > 0) {

                    index - 1
                } else {
                    index
                }
            } else {
                index
            }
        Napier.log(LogLevel.ASSERT, tag = "nextIndex", message = nextIndex.toString())

        iStepPointerRepository.updateActiveActivity(
            ticketNumber = stepPointerDomain.ticketNumber,
            activeActivity = stepListSorted[nextIndex].id
        )


        val data = iStepsRepository.getDataByTicketNumberAndStep(
            stepPointerDomain.ticketNumber,
            stepListSorted.get(nextIndex).id
        ).toActivityDomain()


        try {

            val location = Location.getLastLocation()
            Napier.log(LogLevel.ASSERT, tag = "gpsssss", message = location.latitude)
            Napier.log(LogLevel.ASSERT, tag = "gpsssss", message = location.longitude)

            data.form.form_structure.components?.findComponentByKey("submitted_latitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.latitude))
            data.form.form_structure.components?.findComponentByKey("submitted_longitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.longitude))
            data.form.form_structure.components?.findComponentByKey("submitted_date")?.values =
                arrayListOf(ValueDomain("submitted_date", location.datetime.parsGpsDateTime()))

            params.third.findComponentByKey("submitted_latitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.latitude))
            params.third.findComponentByKey("submitted_longitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.longitude))
            params.third.findComponentByKey("submitted_date")?.values =
                arrayListOf(ValueDomain("submitted_date", location.datetime.parsGpsDateTime()))

        } catch (_: Exception) {

        }
        params.third.createRepeatableSectionStructure(
            ticketNumber = params.first,
            removablesWithParent,
            dict,
            dictImages
        )
        params.third.findImageComponents(params.first).getKeysAndValues(dictImages)
        params.third.findComponentsByType(FormViewerTypes.FileUpload).getKeysAndValues(dictImages)
        params.third.getKeysAndValues(dict)
        iSendStepsRepository.updateKeyValueStructure(
            stepPointerDomain.ticketNumber,
            stepPointerDomain.activeActivity,
            dict.toJson(),
            dictImages.toJson()
        )

        Napier.log(
            LogLevel.ASSERT,
            tag = "params.fifth",
            message = stepPointerDomain.activeActivity.toString()
        )
        Napier.log(LogLevel.ASSERT, tag = "dict", message = dict.toJson())
        Location.stop()
        return StructureActivity(
            nextIndex,
            stepListSorted[nextIndex].title,
            data,
            stepDetails,
        )
    }


    private suspend fun List<ComponentDomain>.createRepeatableSectionStructure(
        ticketNumber: String,
        removablesWithParent: ArrayList<ComponentDomain>,
        dict: MutableMap<String, Any>,
        dictImages: MutableMap<String, Any>
    ) {
        //find removables with their parents
        Napier.log(
            LogLevel.ASSERT,
            tag = "createRepeatableSectionStructure all",
            message = this.toString()
        )

        this.findRemovables(removablesWithParent)
        removablesWithParent.findParentsOfRemovables(this)
        Napier.log(
            LogLevel.ASSERT,
            tag = "createRepeatableSectionStructure",
            message = removablesWithParent.toString()
        )
        removablesWithParent.findImageComponents(ticketNumber)
            .getKeysAndValuesForRepeatableSections(dictImages)
        removablesWithParent.findComponentsByType(FormViewerTypes.FileUpload)
            .getKeysAndValuesForRepeatableSections(dictImages)
        removablesWithParent.getKeysAndValuesForRepeatableSections(dict)
    }

    fun List<ComponentDomain>.findRemovables(removableWithParent: ArrayList<ComponentDomain>) {
        this.forEach {
            if (it.removable == true && it.type != FormViewerTypes.Group)
                removableWithParent.add(it)

            it.components?.findRemovables(removableWithParent)
        }
    }

    fun ArrayList<ComponentDomain>.findParentsOfRemovables(allComponents: List<ComponentDomain>) {
        this.groupBy { it.key }.forEach {
            allComponents.findComponentParentByKeys(it.key ?: "")?.let { parent ->
                this.add(0, parent)
            }
        }
    }

    private fun List<ComponentDomain>.findComponentParentByKeys(key: String): ComponentDomain? {
        this.forEach { component ->
            if (component.key == key && component.removable == false) {
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

    fun List<ComponentDomain>.getKeysAndValuesForRepeatableSections(dict: MutableMap<String, Any>) {
        this.forEach { componentDomain ->
            if (this.isNotEmpty()) {
                if (componentDomain.isSelectable()) {
                    if (componentDomain.shouldBeArray()) {
                        componentDomain.addSelectableItemsArrayFromRemovables(
                            this.filter { it.key == componentDomain.key },
                            dict
                        )
                    } else {
                        componentDomain.addSelectableItemFromRemovables(
                            this.filter { it.key == componentDomain.key },
                            dict
                        )

                    }
                } else {
                    if (componentDomain.shouldBeArray()) {
                        componentDomain.addItemsArrayFromRemovables(
                            this.filter { it.key == componentDomain.key },
                            dict
                        )

                    } else {
                        componentDomain.addItemsFromRemovables(
                            this.filter { it.key == componentDomain.key },
                            dict
                        )

                    }
                }
            }
        }
    }

    fun ComponentDomain.addSelectableItemsArrayFromRemovables(
        list: List<ComponentDomain>,
        dict: MutableMap<String, Any>
    ) {
        dict[this.key ?: ""] = arrayListOf<List<String>>()
        dict[this.key] as ArrayList<ArrayList<String>>
        list.groupBy { it.id }.toList().map {
            it.second.map {
                it.values?.let { nonNullValues ->
                    nonNullValues.filter { it.isSelected }.map { it.value ?: "" }
                } ?: run {
                    listOf()
                }
            }.forEach { listOflistOfString ->
                (dict[this.key] as ArrayList<List<String>>).add(listOflistOfString)
            }
        }
    }


    fun ComponentDomain.addItemsArrayFromRemovables(
        list: List<ComponentDomain>,
        dict: MutableMap<String, Any>
    ) {
        dict[this.key ?: ""] = arrayListOf<List<String>>()
        dict[this.key] as ArrayList<ArrayList<String>>
        list.groupBy { it.id }.toList().map {
            it.second.map {
                it.values?.let { nonoNullValues ->
                    nonoNullValues.map { it.value ?: "" }
                } ?: run {
                    listOf()
                }
            }.forEach { listOflistOfString ->
                (dict[this.key] as ArrayList<List<String>>).add(listOflistOfString)
            }
        }

    }

    fun ComponentDomain.addItemsFromRemovables(
        list: List<ComponentDomain>,
        dict: MutableMap<String, Any>
    ) {
        dict[this.key ?: ""] = arrayListOf<String>()
        list.map {
            it.values?.map { it.value ?: "" }?.forEach { listOfString ->
                (dict[this.key] as ArrayList<String>).add(
                    listOfString
                )
            }
        }
    }

    fun ComponentDomain.addSelectableItemFromRemovables(
        list: List<ComponentDomain>,
        dict: MutableMap<String, Any>
    ) {
        dict[this.key ?: ""] = arrayListOf<String>()
        (dict[this.key] as ArrayList<String>).addAll(list.map {
            it.values?.firstOrNull { it.isSelected }?.let { selectedValue ->
                selectedValue.value ?: ""
            } ?: run {
                ""
            }
        })
    }

    fun ComponentDomain.shouldBeArray(): Boolean {
        return when (type) {
            FormViewerTypes.Select, FormViewerTypes.Multi -> isMulti
            FormViewerTypes.Checklist, FormViewerTypes.FileUpload, FormViewerTypes.ImageView -> true
            else -> false
        }
    }

    fun ComponentDomain.isSelectable() =
        this.type == FormViewerTypes.Checklist || this.type == FormViewerTypes.Radio || this.type == FormViewerTypes.Select || this.type == FormViewerTypes.Multi

    fun ComponentDomain.addSelectableItems(dict: MutableMap<String, Any>) {
        if (this.values?.filter { it.isSelected }?.isNotEmpty() == true) {
            if (dict[this.key] == null) {
                dict[this.key ?: ""] = arrayListOf<String>()
                this.values?.forEach { value ->
                    if (value.isSelected)
                        (dict[this.key] as ArrayList<String>).add(
                            value.value ?: ""
                        )
                }
            }
        }
    }

    fun ComponentDomain.addSelectableItem(dict: MutableMap<String, Any>) {
        Napier.log(LogLevel.ASSERT, tag = "addSelectableItem", message = this.key.toString())
        if (dict[this.key] == null) {
            this.values?.forEach {
                if (it.isSelected)
                    dict[this.key ?: ""] =
                        it.value.toString()
            }
        }

    }

    fun ComponentDomain.addItems(dict: MutableMap<String, Any>) {
        if (dict[this.key] == null) {

            dict[this.key ?: ""] = arrayListOf<String>()
            this.values?.forEach { value ->
                (dict[this.key] as ArrayList<String>).add(
                    value.value ?: ""
                )
            }
        }
    }

    fun ComponentDomain.addItem(dict: MutableMap<String, Any>) {
        if (dict[this.key] == null) {
            dict[this.key ?: ""] =
                this.values?.get(0)?.value.toString()
        }
    }

    fun List<ComponentDomain>.getKeysAndValues(dict: MutableMap<String, Any>) {
        this.forEach { componentDomain ->
            componentDomain.values?.let { values ->
                if (values.isNotEmpty()) {
                    if (componentDomain.isSelectable()) {
                        if (componentDomain.shouldBeArray())
                            componentDomain.addSelectableItems(dict)
                        else
                            componentDomain.addSelectableItem(dict)


                    } else {
                        if (componentDomain.shouldBeArray())
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

    private fun List<ComponentDomain>.hasRemovableObject(component: ComponentDomain): Boolean {
        this.forEach { componentIterable ->
            if (componentIterable.key == component.key && componentIterable.removable == true) return true


            // Recursively search in the children
            return component.components?.hasRemovableObject(component) ?: false
        }
        return false
    }

    fun List<ComponentDomain>.findComponentsByType(type: String): List<ComponentDomain> {
        return this.flatMap { component ->
            listOf(component).plus(component.components?.findComponentsByType(type) ?: emptyList())
        }.filter { it.type == type }
    }

    private suspend fun List<ComponentDomain>.findImageComponents(ticket_number: String): List<ComponentDomain> {
        val imageComponents = this.findComponentsByType(FormViewerTypes.ImageView)
        val photoDomainList = iPhotoRepository.getTicketProcessPhotos(
            ticket_number,
            imageComponents.mapNotNull { it.key }).toPhotoDomainList()

        imageComponents.forEach { component ->
            val newValues = mutableListOf<ValueDomain>()
            photoDomainList.forEach { photoDomain ->
                if (component.key == photoDomain.component_key && component.id == photoDomain.componentId) {
                    if (photoDomain.origin_uri.isNotEmpty())
                        newValues.add(ValueDomain(value = photoDomain.origin_uri))
                    if (photoDomain.edited_uri.isNotEmpty())
                        newValues.add(ValueDomain(value = photoDomain.edited_uri))
                }
            }
            component.values = newValues
        }
        return imageComponents
    }

}

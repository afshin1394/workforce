package domain.usecase.usecase.steps

import arrow.core.Tuple4
import arrow.core.Tuple5
import data.network.response.task.Component
import data.network.response.task.FormStruct
import database.entity.SendStepsEntity
import domain.mappers.toComponent
import domain.mappers.toPhotoDomainList
import domain.mappers.toPhotoEntityList
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.models.form_struct.ValueDomain
import domain.repository.IPhotoRepository
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import toActivityDomain
import utils.FormViewerTypes
import utils.LogicCalculation
import utils.parsGpsDateTime
import utils.toJson

class StoreStepFormUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository,
    private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, Tuple5<String, List<ComponentDomain>, List<PhotoDomain>, Int, String>>() {


    override suspend fun run(params: Tuple5<String, List<ComponentDomain>, List<PhotoDomain>, Int, String>) {
        Location.start { }
        val removablesWithParent: ArrayList<ComponentDomain> = arrayListOf()
        val dict = mutableMapOf<String, Any>()
        val dictImages = mutableMapOf<String, Any>()

        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)
        iStepsRepository.updateFormStructure(
            params.first, stepPointerDomain.activeActivity, Json.encodeToString(
                FormStruct.serializer(), FormStruct(components = (params.second.toComponent()))
            )
        )

        try {
            val location = Location.getLastLocation()
            params.second.findComponentByKey("submitted_latitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.latitude))
            params.second.findComponentByKey("submitted_longitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.longitude))
            params.second.findComponentByKey("submitted_date")?.values =
                arrayListOf(ValueDomain("submitted_date", location.datetime.parsGpsDateTime()))
        } catch (e: Exception) {

        }
        params.second.let {
            val logicCalculation = LogicCalculation(
                CoroutineScope(Dispatchers.IO),
                it
            )
            logicCalculation.ticketId = params.fifth
            checkLogicsForAll(logicCalculation,it)
        }


        params.second.createRepeatableSectionStructure(params.first,removablesWithParent,dict,dictImages)
        params.second.findImageComponents(params.first).getKeysAndValues(dictImages)
        params.second.findComponentsByType(FormViewerTypes.FileUpload).getKeysAndValues(dictImages)
        params.second.getKeysAndValues(dict)
        iSendStepsRepository.updateKeyValueStructure(
            stepPointerDomain.ticketNumber,
            stepPointerDomain.activeActivity,
            dict.toJson(),
            dictImages.toJson()
        )


        Napier.log(LogLevel.ASSERT, "keyValue ", message = dict.toJson())
        Location.stop()

    }

    private suspend fun  List<ComponentDomain>.createRepeatableSectionStructure(ticketNumber : String, removablesWithParent: ArrayList<ComponentDomain>, dict: MutableMap<String, Any>, dictImages: MutableMap<String,Any>){
        //find removables with their parents
        Napier.log(LogLevel.ASSERT, tag = "createRepeatableSectionStructure all", message =  this.toString())

        this.findRemovables(removablesWithParent)
        removablesWithParent.findParentsOfRemovables(this)
        Napier.log(LogLevel.ASSERT, tag = "createRepeatableSectionStructure", message =  removablesWithParent.toString())
        removablesWithParent.findImageComponents(ticketNumber)
            .getKeysAndValuesForRepeatableSections(dictImages)
        removablesWithParent.findComponentsByType(FormViewerTypes.FileUpload)
            .getKeysAndValuesForRepeatableSections(dictImages)
        removablesWithParent.getKeysAndValuesForRepeatableSections(dict)
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


    fun List<ComponentDomain>.findComponentsByType(type: String): List<ComponentDomain> {
        return this.flatMap { component ->
            listOf(component).plus(component.components?.let { it.findComponentsByType(type) }
                ?: emptyList())
        }.filter { it.type == type }
    }

    private suspend fun List<ComponentDomain>.findImageComponents(ticket_number: String): List<ComponentDomain> {
        val imageComponents = this.findComponentsByType(FormViewerTypes.ImageView)
        val photoDomainList = iPhotoRepository.getTicketProcessPhotos(
            ticket_number,
            imageComponents.mapNotNull { it.key }).toPhotoDomainList()
        Napier.log(LogLevel.ASSERT, tag = "findImageComponents", message = photoDomainList.toString())
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
        Napier.log(LogLevel.ASSERT, tag = "findImageComponents", message = imageComponents.toString())

        return imageComponents
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
                this.add(0,parent)
            }
        }
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
            }?:run{
                ""
            }
        })
    }

    private suspend fun checkLogicsForAll(logicCalculation: LogicCalculation, components: List<ComponentDomain>) {

        // Create a copy of the components list to iterate over
        val componentsCopy = components.toMutableList()

        for (cmp in componentsCopy) {
            logicCalculation.extractLogics(componentsCopy, cmp)
            cmp.components?.let { cmps ->
                if (cmps.isNotEmpty()) {
                    checkLogicsForAll(logicCalculation,cmps)

                }
            }
        }


    }
}








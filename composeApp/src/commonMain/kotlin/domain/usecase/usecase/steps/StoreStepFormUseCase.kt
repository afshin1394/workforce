package domain.usecase.usecase.steps

import arrow.core.Tuple4
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import toActivityDomain
import utils.FormViewerTypes
import utils.toJson

class StoreStepFormUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository,
    private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, Tuple4<String, List<ComponentDomain>, List<PhotoDomain>,Int>>() {

    override suspend fun run(params: Tuple4<String, List<ComponentDomain>, List<PhotoDomain>, Int>) {
        val dict = mutableMapOf<String, Any>()
        val dictImages = mutableMapOf<String,Any>()

        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)
        iStepsRepository.updateFormStructure(
            params.first, stepPointerDomain.activeActivity, Json.encodeToString(
                FormStruct.serializer(), FormStruct(components = (params.second.toComponent()))
            ), Json.encodeToString(params.third)
        )
//        val data = iStepsRepository.getDataByTicketNumberAndStep(
//            stepPointerDomain.ticketNumber,
//            stepPointerDomain.activeActivity
//        ).toActivityDomain()
        try {
            val location = Location.getLastLocation()
            params.second.findComponentByKey("submitted_latitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.latitude))
            params.second.findComponentByKey("submitted_longitude")?.values =
                arrayListOf(ValueDomain("submitted_latitude", location.longitude))
//            data.form.form_structure.components?.findComponentByKey("submitted_date")?.values =
//                arrayListOf(ValueDomain("submitted_date", location.datetime))
        } catch (e: Exception) {

        }


        params.second.findImageComponents().getKeysAndValues(dictImages)
        Napier.log(LogLevel.ASSERT, tag = "dicttttt", message = dictImages.toString())
        params.second.filter { it.type != FormViewerTypes.ImageView }.getKeysAndValues(dict)
        iSendStepsRepository.updateKeyValueStructure(
            stepPointerDomain.ticketNumber,
            stepPointerDomain.activeActivity,
            dict.toJson(),
            dictImages.toJson()
        )


        Napier.log(LogLevel.ASSERT, "keyValue ", message = dict.toJson())

    }

    fun ComponentDomain.isSelectable() =
        this.type == FormViewerTypes.Checklist || this.type == FormViewerTypes.Radio || this.type == FormViewerTypes.Select


    fun ComponentDomain.addSelectableItems(dict: MutableMap<String, Any>) {
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

    fun ComponentDomain.addSelectableItem(dict: MutableMap<String, Any>) {
        if (this.values?.get(0)?.isSelected == true)
            dict[this.key ?: ""] =
                this.values?.get(0)?.value.toString()
    }

    fun ComponentDomain.addItems(dict: MutableMap<String, Any>) {
        dict[this.key ?: ""] = arrayListOf<String>()
        this.values?.forEach { value ->
            (dict[this.key] as ArrayList<String>).add(
                value.value ?: ""
            )
        }
    }

    fun ComponentDomain.addItem(dict: MutableMap<String, Any>) {
        dict[this.key ?: ""] =
            this.values?.get(0)?.value.toString()
    }

    fun List<ComponentDomain>.getKeysAndValues(dict: MutableMap<String, Any>) {
        this.forEach { componentDomain ->
            componentDomain.values?.let { values ->
                if (values.isNotEmpty()) {
                    if (componentDomain.isSelectable()) {
                        if (values.filter { it.isSelected }.size > 1)
                            componentDomain.addSelectableItems(dict)
                        else
                            componentDomain.addSelectableItem(dict)


                    } else {
                        if (values.size > 1)
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

    fun  List<ComponentDomain>.findComponentsByType( type: String): List<ComponentDomain> {
        return this.flatMap { component ->
            listOf(component).plus(component.components?.let {it.findComponentsByType(type) } ?: emptyList())
        }.filter { it.type == type }
    }

   private suspend fun List<ComponentDomain>.findImageComponents() : List<ComponentDomain>{
        val imageComponents = this.findComponentsByType(FormViewerTypes.ImageView)
        val photoDomainList  = iPhotoRepository.getTicketProcessPhotos(imageComponents.mapNotNull { it.key }).toPhotoDomainList()

        imageComponents.forEach {
                component->
            val newValues =  mutableListOf<ValueDomain>()
            photoDomainList.forEach {
                    photoDomain ->
                if (component.key == photoDomain.component_key){
                    if(photoDomain.origin_uri.isNotEmpty())
                        newValues.add(ValueDomain(value =  photoDomain.origin_uri))
                    if(photoDomain.edited_uri.isNotEmpty())
                        newValues.add(ValueDomain(value =  photoDomain.edited_uri))
                }
            }
            component.values = newValues
        }
       return imageComponents
    }


}








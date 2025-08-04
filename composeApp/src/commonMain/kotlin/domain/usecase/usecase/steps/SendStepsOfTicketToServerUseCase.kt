package domain.usecase.usecase.steps


import data.network.request.step.StepRequest
import data.network.request.step.SubmitAllRequest
import domain.mappers.toPhotoDomainList
import domain.mappers.toUploadDomainList
import domain.models.UploadDomain
import domain.models.form_struct.ComponentDomain
import domain.repository.IPhotoRepository
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.repository.IUploadRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.InternalStorage
import irancell.nwg.wfm.provideAppContext
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import domain.mappers.toActivityDomainList
import utils.FormViewerTypes
import utils.convertToZip
import utils.formatUploadDomainList
import utils.mutableToJson
import utils.processInParallel

class SendStepsOfTicketToServerUseCase(
    private val iSendStepsRepository: ISendStepsRepository,
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iPhotoRepository: IPhotoRepository,
    private val iUploadRepository: IUploadRepository,
) :
    BaseUseCase<Unit, String>() {
    override suspend fun run(params: String) {

        val sendStepsEntity = iSendStepsRepository.getStepsByTicketNumber(ticketNumber = params)
        val stepsEntities = iStepsRepository.getStepsByTicketNumber(params).toActivityDomainList()
        val photoDomainList =
            iPhotoRepository.getTicketProcessPhotosWithoutSuspends(params).toPhotoDomainList()
        iPhotoRepository.deleteProcessImages(params)

        val sortedSendSteps = sendStepsEntity.sortedBy { it.activityId }
        val submitAllRequest = SubmitAllRequest(ticket_num = params, steps = arrayListOf())
        val uploadDomains = mutableListOf<UploadDomain>()

        processInParallel(
            items = sendStepsEntity,
            processBlock = { step, mutex ->
                val map = try {
                    jsonToMap(step.key_value_image_structure)
                } catch (e: Exception) {
                    Napier.log(
                        LogLevel.ERROR,
                        tag = "jsonExceptionPlusTurbo",
                        message = e.message.orEmpty()
                    )
                    Napier.log(
                        LogLevel.INFO,
                        tag = "jsonExceptionPlusTurbo",
                        message = step.key_value_image_structure
                    )
                    null
                }
                mutex.withLock {
                    map?.let { validMap ->
                        val uploadDomainsForMap = validMap.let(::mapToList).flatMap { group ->
                            val zipData = convertToZip(listOf(group), "testt", params)
                            iUploadRepository.fetchUpload(zipData).toUploadDomainList()
                        }
                        uploadDomains.addAll(uploadDomainsForMap)
                    }
                }
            }
        )

        val distinctUploadDomains = uploadDomains.distinct()
        Napier.log(
            LogLevel.ASSERT,
            tag = "uploadDomains",
            message = "uploadDomains size: ${distinctUploadDomains.size}"
        )


        val formattedList = formatUploadDomainList(uploadDomains)

        val imageMap = listToMap(formattedList)
        Napier.log(LogLevel.ASSERT, tag = "imageMap", message = imageMap.toString())


        sortedSendSteps.forEachIndexed { index, item ->
            if (index != 0) {
                try {
                    val map = jsonToMap(
                        item.key_value_structure.replace("}\"", "}").replace("\"{", "{")
                            .replace("\\", "")
                    )
                    updateCommonKeys(map, imageMap)
                    submitAllRequest.steps
                        .add(StepRequest(item.activityId.toInt(), map.mutableToJson(), 0))
                } catch (e: Exception) {
                    Napier.log(
                        LogLevel.ERROR,
                        tag = "jsonExceptionPlusTurbo",
                        message = e.toString()
                    )
                    Napier.log(
                        LogLevel.INFO,
                        tag = "jsonExceptionPlusTurbo",
                        message = item.key_value_structure
                    )
                }
            } else {
                try {
                    val map =
                        jsonToMap(
                            item.key_value_structure.replace("}\"", "}").replace("\"{", "{")
                                .replace("\\", "")
                        )
                    updateCommonKeys(map, imageMap)
                    submitAllRequest.steps.add(
                        StepRequest(
                            item.activityId.toInt(),
                            map.mutableToJson(),
                            item.wi.toInt()
                        )
                    )
                } catch (e: Exception) {
                    Napier.log(
                        LogLevel.ERROR,
                        tag = "jsonExceptionPlusTurbo",
                        message = e.toString()
                    )
                    Napier.log(
                        LogLevel.INFO,
                        tag = "jsonExceptionPlusTurbo",
                        message = item.key_value_structure
                    )
                }
            }
        }
//
//

        Napier.log(
            LogLevel.ASSERT,
            tag = "submitAllRequest",
            message = Json.encodeToString(SubmitAllRequest.serializer(), submitAllRequest)
                .replace("}\"", "}").replace("\"{", "{").replace("\\", "")
        )

        iSendStepsRepository.sendData(
            Json.encodeToString(
                SubmitAllRequest.serializer(),
                submitAllRequest
            ).replace("}\"", "}").replace("\"{", "{").replace("\\", "")
        )

        //Remove local files after sending them to remote server
        InternalStorage.removeFiles(photoDomainList)
        InternalStorage.removeFiles(listOf(InternalStorage.getWFMRoute(provideAppContext()) + "files.zip"))
        stepsEntities.forEach { activityDomain ->
            activityDomain.form.form_structure.components?.findComponentsByType(FormViewerTypes.FileUpload)
                ?.forEach { component ->
                    InternalStorage.removeFiles(component.values)
                }
        }

        iSendStepsRepository.deleteAllSendSteps(arrayListOf(params))
        iStepsRepository.deleteAllSteps(arrayListOf(params))
        iStepPointerRepository.deleteAllStepPointers(arrayListOf(params))
    }

    private fun List<ComponentDomain>.findComponentsByType(type: String): List<ComponentDomain> {
        return this.flatMap { component ->
            listOf(component).plus(
                component.components.value?.findComponentsByType(type) ?: emptyList()
            )
        }.filter { it.type == type }
    }
}


fun jsonToMap(jsonString: String): MutableMap<String, Any> {
    val jsonElement = Json.parseToJsonElement(jsonString)
    return jsonElement.jsonObject.toMutableMap()
}

fun mapToList(map: Map<String, Any>): List<String> {
    val listOfImages = hashSetOf<String>()
    map.forEach { it ->
        val key = it.key
        val values = map[key] as List<*>
        values.forEach { value ->
            listOfImages.add(
                (value as JsonPrimitive).content.replace(" ", "").replace("(", "")
                    .replace(")", "").replace("$", "")
            )
        }

    }
    return listOfImages.toList()
}

fun listToMap(uploadList: List<UploadDomain>): MutableMap<String, Any> {


    val resultMap = mutableMapOf<String, Any>()

    // Grouping the list by the key
    val groupedMap = uploadList.filter { it.key != null && it.value != null }
        .groupBy { it.key!! }

    // Iterating through each group
    for ((key, uploadDomains) in groupedMap) {
        // If there are multiple values, add them as a List<String>
        resultMap[key] = uploadDomains.map { it.value ?: "" }

    }

    return resultMap
}

fun updateCommonKeys(map1: MutableMap<String, Any>, map2: MutableMap<String, Any>) {
    // Iterate over the keys of map1
    for (key in map1.keys) {
        // Check if map2 contains the same key
        if (map2.containsKey(key)) {
            // Replace the value in map1 with the value from map2
            map2[key]?.let {
                map1[key] = it
            }
        }
    }

}

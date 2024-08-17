package domain.usecase.usecase.steps

import arrow.core.sort
import data.network.request.step.StepRequest
import data.network.request.step.SubmitAllRequest
import domain.mappers.toUploadDomainList
import domain.models.UploadDomain
import domain.repository.ISendStepsRepository
import domain.repository.IUploadRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import utils.convertToZip
import utils.formatUploadDomainList
import utils.mutableToJson

class SendStepsOfTicketToServerUseCase(
    private val iSendStepsRepository: ISendStepsRepository,
    private val iUploadRepository: IUploadRepository,
) :
    BaseUseCase<Unit, String>() {
    override suspend fun run(params: String) {

        val sendStepsEntity = iSendStepsRepository.getStepsByTicketNumber(ticketNumber = params)
        val sortedSendSteps = sendStepsEntity.sortedBy { it.activityId }
        val submitAllRequest = SubmitAllRequest(ticket_num = params, steps = arrayListOf())
        val listOfString = arrayListOf<String>()
        sendStepsEntity.forEachIndexed { index, sendStepsEntity ->
            try {
                val map = jsonToMap(
                    sendStepsEntity.key_value_image_structure
                )

                listOfString.addAll(mapToList(map))
            } catch (e: Exception) {
                Napier.log(LogLevel.ASSERT, tag = "listOfString", message = e.toString())
            }
        }
        val data = iUploadRepository.fetchUpload(convertToZip(list = listOfString, "testt", params))
            .toUploadDomainList()
        val formattedList = formatUploadDomainList(data)

        val imageMap = listToMap(formattedList)
        Napier.log(LogLevel.ASSERT, tag = "imageMap", message = imageMap.toString())

//
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
                } catch (_: Exception) {

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
                } catch (_: Exception) {

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
}

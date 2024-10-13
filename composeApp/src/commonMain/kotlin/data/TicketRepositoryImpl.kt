package data

import data.network.request.ticket.TicketDetailRequest
import data.network.response.ticket.TicketDetailResponse
import domain.repository.ITicketRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TicketRepositoryImpl(
    private val httpClient: HttpClient,
) : ITicketRepository {
    override suspend fun fetchTicketDetails(ticketId : String,ticketDetailRequest: TicketDetailRequest): Map<String,String> {
      val details = httpClient.post("ticket-instance/${ticketId}/details/"){
            setBody(ticketDetailRequest)
        }.bodyAsText()
        val finalMap = mutableMapOf<String,String>()
        val map : MutableMap<String,JsonElement> = Json.parseToJsonElement(details).jsonObject.toMutableMap()
        map.mapValues {
          val temp  = it.value.jsonObject.toMutableMap()
            temp.mapValues { value->
                val content : String? = if(value.value is JsonPrimitive)
                 value.value.jsonPrimitive.contentOrNull
                else
                    (value.value as JsonArray)
                        .jsonArray
                        .map { it.jsonPrimitive.contentOrNull }
                        .joinToString(",")
                content?.let {
                    finalMap.put(value.key,content)
                }
            }
        }
        Napier.log(LogLevel.ASSERT, tag = "finalMap", message =  finalMap.toString())
        return finalMap
    }
}
package data

import data.network.response.task.step.TaskStepResponse
import domain.repository.IStepsRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

class StepsRepositoryImpl(
    private val httpClient: HttpClient,
    ) : IStepsRepository {
    override suspend fun fetch(queryParam: String): TaskStepResponse {
      val request = httpClient.get("workforce_management/user/steps/") {
            parameter("search_p", queryParam)
        }
        if (request.status == HttpStatusCode.OK) {
            return try {
                Napier.log(LogLevel.ASSERT,tag = "StepsRepositoryImpl", message =  request.body<TaskStepResponse>().toString())
                request.body<TaskStepResponse>()
            } catch (exception: Exception){
                Napier.log(LogLevel.ASSERT,tag = "StepsRepositoryImpl", message =  exception.message.toString())
                TaskStepResponse(listOf())
            }
        }
        throw Exception()
    }
}
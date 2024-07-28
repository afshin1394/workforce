package data

import data.network.response.task.step.TaskStepResponse
import database.AppDatabase
import database.dao.StepsDao
import database.entity.StepsEntity
import database.entity.TaskEntity
import domain.models.steps.ActivityDomain
import domain.models.steps.StepDetailDomain
import domain.repository.IStepsRepository
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import toActivityDomainList

class StepsRepositoryImpl(
    private val httpClient: HttpClient,
    private val db: AppDatabase,
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

    override suspend fun insertAll(tasks : List<StepsEntity>) {
        db.stepDao().insertAll(tasks)
    }

    override suspend fun deleteAll(ticketNumbers: List<String>) {
        db.stepDao().deleteAll(ticketNumbers)
    }

    override suspend fun getStepsByTicketNumber(ticketNumber: String) : List<StepsEntity> {
       return db.stepDao().selectStepsByTicketNumber(ticketNumber)
    }

    override suspend fun getDataByTicketNumberAndStep(ticketNumber: String, activityId: Long) : StepsEntity {
       return db.stepDao().selectStepsByTicketNumberAndActivityId(ticketNumber = ticketNumber, activityId = activityId)
    }

    override suspend fun getEditedTickets() : List<String>{
        val editedTickets = db.stepDao().selectEditedTickets()
        return HashSet(editedTickets).toList()
    }

    override suspend fun resetEntitySequence() {
        db.stepDao().resetSequence()
    }
}
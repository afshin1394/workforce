package data

import data.network.response.task.activity.ActivityListResponse
import data.network.response.task.task.TasksNetworkResponse
import data.network.response.task.typeTask.TicketAllMiniResponse
import database.AppDatabase
import database.entity.ActivityListEntity
import database.entity.TaskEntity
import domain.repository.ITaskRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get


class TaskRepositoryImpl(
    private val httpClient: HttpClient,
    private val db: AppDatabase
) : ITaskRepository {
    override suspend fun fetchWorks(): TasksNetworkResponse {
           return httpClient.get("workforce_management/user/v1/my-tasks/")
                .body<TasksNetworkResponse>()

    }


    override suspend fun fetchActivityList():  List<ActivityListResponse> {
        return httpClient.get("workforce_management/user/activity-list/")
            .body<List<ActivityListResponse>>()

    }
    override suspend fun fetchTicketAllMini(): List<TicketAllMiniResponse> {
        return httpClient
            .get("ticket/all/mini")
            .body<List<TicketAllMiniResponse>>()
    }



    override suspend fun insertAll(tickets: List<TaskEntity>) {
        db.taskDao().insertAll(tickets)
    }


    override suspend fun getAll(): List<TaskEntity> {
        return db.taskDao().selectAll()
    }

    override suspend fun deleteAll() {
        db.taskDao().deleteAll()
    }

    override suspend fun resetEntitySequence() {
        db.taskDao().resetSequence()
    }


    override suspend fun getTaskByTicketNumber(ticketNumber: String): TaskEntity? {
        return db.taskDao().getTaskByTicketNumber(ticketNumber)
    }

    override suspend fun insertAllActivityList(activityList: List<ActivityListEntity>) {
        db.taskDao().insertAllActivityList(activityList)
    }

    override suspend fun getAllActivityList(): List<ActivityListEntity> {
        return db.taskDao().selectAllActivityList()
    }
}
package data

import data.network.response.task.task.TasksNetworkResponse
import database.AppDatabase
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
           return httpClient.get("workforce_management/user/my-tasks/")
                .body<TasksNetworkResponse>()
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


}
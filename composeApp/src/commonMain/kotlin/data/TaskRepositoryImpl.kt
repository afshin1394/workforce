package data

import data.network.response.task.task.TasksNetworkResponse
import domain.repository.ITaskRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import irancell.nwg.wfm.db.TaskEntity
import irancell.nwg.wfm.db.WFMDatabase

class TaskRepositoryImpl(
    private val httpClient: HttpClient,
    private val wfmDatabase: WFMDatabase
) : ITaskRepository {
    override suspend fun fetchWorks(): TasksNetworkResponse {
           return httpClient.get("workforce_management/user/my-tasks/")
                .body<TasksNetworkResponse>()
    }

    override suspend fun insertAll(tickets: List<TaskEntity>) {
        wfmDatabase.taskEntityQueries.transaction {
            val insertStatement = wfmDatabase.taskEntityQueries::insert
            tickets.forEach {
                insertStatement.invoke(
                    it.ticket_number,
                    it.ticket_state,
                    it.level,
                    it.location,
                    it.site,
                    it.region,
                    it.province,
                    it.city,
                )
            }
        }
    }

    override suspend fun getAll(): List<TaskEntity> {
       return wfmDatabase.taskEntityQueries.selectAll().executeAsList()
    }

    override suspend fun deleteAll() {
        wfmDatabase.taskEntityQueries.deleteAll()
    }


}
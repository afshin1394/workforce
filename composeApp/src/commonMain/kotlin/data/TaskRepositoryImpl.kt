package data

import data.network.response.task.TasksNetworkResponse
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
    override suspend fun fetchWorks(): List<TasksNetworkResponse> {
           return httpClient.get("workforce_management/user/my-tasks/")
                .body<List<TasksNetworkResponse>>()
    }

    override suspend fun insertAll(tickets: List<TaskEntity>) {
        wfmDatabase.taskEntityQueries.transaction {
            val insertStatement = wfmDatabase.taskEntityQueries::insert
            tickets.forEach {
                insertStatement.invoke(
                    it.wi_id,
                    it.ticket_instance_id,
                    it.ticket_title,
                    it.ticket_instance_number,
                    it.ticket_instance_state,
                    it.ticket_instance_title,
                    it.type,
                    it.description,
                    it.category,
                    it.subCategory,
                    it.attachment
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
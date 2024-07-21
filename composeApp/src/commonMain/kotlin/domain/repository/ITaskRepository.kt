package domain.repository

import data.network.response.task.task.TasksNetworkResponse
import irancell.nwg.wfm.db.TaskEntity

interface ITaskRepository {
  suspend  fun fetchWorks() : TasksNetworkResponse

  suspend fun insertAll(tickets : List<TaskEntity>)

  suspend fun getAll() : List<TaskEntity>

  suspend fun deleteAll()

}
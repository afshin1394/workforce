package domain.repository

import data.network.response.task.TasksNetworkResponse
import irancell.nwg.wfm.db.TaskEntity

interface ITaskRepository {
  suspend  fun fetchWorks() : List<TasksNetworkResponse>

  suspend fun insertAll(tickets : List<TaskEntity>)

  suspend fun getAll() : List<TaskEntity>

  suspend fun deleteAll()

}
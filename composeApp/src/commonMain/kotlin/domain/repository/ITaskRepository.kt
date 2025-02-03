package domain.repository

import data.network.response.task.activity.ActivityListResponse
import data.network.response.task.task.TasksNetworkResponse
import data.network.response.task.typeTask.TicketAllMiniResponse
import database.entity.ActivityListEntity
import database.entity.TaskEntity


interface ITaskRepository {
  suspend  fun fetchWorks() : TasksNetworkResponse
  suspend  fun fetchActivityList() : List<ActivityListResponse>
  suspend  fun fetchTicketAllMini() : List<TicketAllMiniResponse>
  suspend  fun insertAllActivityList(activityList:List<ActivityListEntity>)

  suspend fun insertAll(tickets : List<TaskEntity>)

  suspend fun getAll() : List<TaskEntity>
  suspend fun getAllActivityList() : List<ActivityListEntity>

  suspend fun deleteAll()

  suspend fun resetEntitySequence()

  suspend fun getTaskByTicketNumber(ticketNumber: String): TaskEntity?


}
package domain.repository

import data.network.response.task.activity.ActivityListResponse
import data.network.response.task.task.TasksNetworkResponse
import data.network.response.task.typeTask.TicketAllMiniResponse
import database.entity.ActivityListEntity
import database.entity.TaskEntity


interface ITaskRepository {
  suspend  fun downloadTasks()
  suspend  fun fetchWorks() : TasksNetworkResponse
  suspend  fun fetchActivityList() : List<ActivityListResponse>
  suspend  fun fetchTicketAllMini() : List<TicketAllMiniResponse>
  suspend  fun insertAllActivityList(activityList:List<ActivityListEntity>)

  suspend fun insertAll(tickets : List<TaskEntity>)

  suspend fun getAll() : List<TaskEntity>
  suspend fun getTasksPaginated(limit: Int, offset: Int): List<TaskEntity>
  suspend fun getTaskCount(): Int
  suspend fun searchTasksPaginated(query: String, limit: Int, offset: Int): List<TaskEntity>
  suspend fun getSearchTaskCount(query: String): Int
  suspend fun getAllActivityList() : List<ActivityListEntity>
  suspend fun deleteAllActivityList()
  suspend fun resetActivityListSequence()

  suspend fun deleteAll()
  suspend fun deleteAllExcept(ticketNumbers: List<String>)
  suspend fun deleteSpecific(ticketNumbers: List<String>)

  suspend fun resetEntitySequence()

  suspend fun getTaskByTicketNumber(ticketNumber: String): TaskEntity?


}
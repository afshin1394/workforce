package data

import data.network.BaseRepository
import data.network.NetworkResult
import data.network.addAuthHeader
import data.network.addStandardHeaders
import data.network.response.task.activity.ActivityListResponse
import data.network.response.task.task.TasksNetworkResponse
import data.network.response.task.typeTask.TicketAllMiniResponse
import database.AppDatabase
import database.entity.ActivityListEntity
import database.entity.TaskEntity
import domain.repository.ITaskRepository
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import irancell.nwg.wfm.DownloadState
import irancell.nwg.wfm.PerformGZIPDownload
import utils.DevelopmentBASEURL
import utils.LoggingConfig

class TaskRepositoryImpl(
    private val httpClient: HttpClient,
    private val db: AppDatabase
) : BaseRepository(), ITaskRepository {
    override suspend fun downloadTasks() {
        LoggingConfig.logNetwork("downloadTasks", "Starting GZIP download")
        
        PerformGZIPDownload(
            url = DevelopmentBASEURL + "workforce_management/user/gzip/my-tasks",
            fileName = "tasks.txt"
        ) { downloadState ->
            when (downloadState) {
                is DownloadState.Started -> {
                    LoggingConfig.logNetwork("downloadTasks", "Download started")
                }
                is DownloadState.Progress -> {
                    val percent = downloadState.totalBytes?.let {
                        (downloadState.bytesDownloaded * 100 / it).toInt()
                    } ?: -1
                    LoggingConfig.logNetwork("downloadTasks", "Progress: $percent%")
                }
                is DownloadState.Finished -> {
                    LoggingConfig.logNetwork("downloadTasks", "Download completed successfully")
                }
                is DownloadState.Failed -> {
                    Napier.e("Download failed: ${downloadState.exception.message}", downloadState.exception)
                }
            }
        }
    }

    override suspend fun fetchWorks(): TasksNetworkResponse {
        LoggingConfig.logNetwork("fetchWorks", "Fetching work tasks")
        
        return when (val result = httpClient.getWithResult<TasksNetworkResponse>("workforce_management/user/my-tasks/") {
            addAuthHeader()
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> result.data
            is NetworkResult.Error -> {
                Napier.e("Failed to fetch works: ${result.exception.message}", result.exception)
                throw result.exception
            }
            is NetworkResult.Loading -> throw IllegalStateException("Unexpected loading state")
        }
    }




    override suspend fun fetchActivityList(): List<ActivityListResponse> {
        LoggingConfig.logNetwork("fetchActivityList", "Fetching activity list")
        
        return when (val result = httpClient.getWithResult<List<ActivityListResponse>>("workforce_management/user/activity-list/") {
            addAuthHeader()
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> result.data
            is NetworkResult.Error -> {
                Napier.e("Failed to fetch activity list: ${result.exception.message}", result.exception)
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }
    override suspend fun fetchTicketAllMini(): List<TicketAllMiniResponse> {
        LoggingConfig.logNetwork("fetchTicketAllMini", "Fetching mini tickets")
        
        return when (val result = httpClient.getWithResult<List<TicketAllMiniResponse>>("ticket/all/mini") {
            addAuthHeader()
            addStandardHeaders()
        }) {
            is NetworkResult.Success -> result.data
            is NetworkResult.Error -> {
                Napier.e("Failed to fetch mini tickets: ${result.exception.message}", result.exception)
                emptyList()
            }
            is NetworkResult.Loading -> emptyList()
        }
    }



    override suspend fun insertAll(tickets: List<TaskEntity>) {
        try {
            LoggingConfig.logUseCase("TaskRepository", "Inserting ${tickets.size} tasks")
            db.taskDao().insertAll(tickets)
        } catch (e: Exception) {
            Napier.e("Failed to insert tasks", e)
            throw e
        }
    }


    override suspend fun getAll(): List<TaskEntity> {
        return try {
            db.taskDao().selectAll()
        } catch (e: Exception) {
            Napier.e("Failed to get all tasks", e)
            emptyList()
        }
    }

    override suspend fun getTasksPaginated(limit: Int, offset: Int): List<TaskEntity> {
        return try {
            LoggingConfig.logUseCase("TaskRepository", "Getting tasks paginated: limit=$limit, offset=$offset")
            db.taskDao().selectPaginated(limit, offset)
        } catch (e: Exception) {
            Napier.e("Failed to get tasks paginated", e)
            emptyList()
        }
    }

    override suspend fun getTaskCount(): Int {
        return try {
            db.taskDao().getTaskCount()
        } catch (e: Exception) {
            Napier.e("Failed to get task count", e)
            0
        }
    }

    override suspend fun searchTasksPaginated(query: String, limit: Int, offset: Int): List<TaskEntity> {
        return try {
            LoggingConfig.logUseCase("TaskRepository", "Searching tasks paginated: query='$query', limit=$limit, offset=$offset")
            db.taskDao().searchTasksPaginated(query, limit, offset)
        } catch (e: Exception) {
            Napier.e("Failed to search tasks paginated", e)
            emptyList()
        }
    }

    override suspend fun getSearchTaskCount(query: String): Int {
        return try {
            db.taskDao().getSearchTaskCount(query)
        } catch (e: Exception) {
            Napier.e("Failed to get search task count", e)
            0
        }
    }

    override suspend fun deleteAll() {
        try {
            LoggingConfig.logUseCase("TaskRepository", "Deleting all tasks")
            db.taskDao().deleteAll()
        } catch (e: Exception) {
            Napier.e("Failed to delete all tasks", e)
            throw e
        }
    }

    override suspend fun deleteAllExcept(ticketNumbers: List<String>) {
        try {
            LoggingConfig.logUseCase("TaskRepository", "Deleting tasks except ${ticketNumbers.size} modified ones")
            if (ticketNumbers.isEmpty()) {
                db.taskDao().deleteAll()
            } else {
                db.taskDao().deleteAllExcept(ticketNumbers)
            }
        } catch (e: Exception) {
            Napier.e("Failed to delete tasks except specified ones", e)
            throw e
        }
    }

    override suspend fun deleteSpecific(ticketNumbers: List<String>) {
        try {
            LoggingConfig.logUseCase("TaskRepository", "Deleting ${ticketNumbers.size} specific tasks")
            if (ticketNumbers.isNotEmpty()) {
                db.taskDao().deleteSpecific(ticketNumbers)
            }
        } catch (e: Exception) {
            Napier.e("Failed to delete specific tasks", e)
            throw e
        }
    }

    override suspend fun resetEntitySequence() {
        try {
            db.taskDao().resetSequence()
        } catch (e: Exception) {
            Napier.e("Failed to reset sequence", e)
            throw e
        }
    }


    override suspend fun getTaskByTicketNumber(ticketNumber: String): TaskEntity? {
        return try {
            db.taskDao().getTaskByTicketNumber(ticketNumber)
        } catch (e: Exception) {
            Napier.e("Failed to get task by ticket number: $ticketNumber", e)
            null
        }
    }

    override suspend fun insertAllActivityList(activityList: List<ActivityListEntity>) {
        try {
            LoggingConfig.logUseCase("TaskRepository", "Inserting ${activityList.size} activities")
            db.taskDao().insertAllActivityList(activityList)
        } catch (e: Exception) {
            Napier.e("Failed to insert activity list", e)
            throw e
        }
    }

    override suspend fun getAllActivityList(): List<ActivityListEntity> {
        return try {
            db.taskDao().selectAllActivityList()
        } catch (e: Exception) {
            Napier.e("Failed to get all activity list", e)
            emptyList()
        }
    }

    override suspend fun deleteAllActivityList() {
        try {
            db.taskDao().deleteAllActivityList()
        } catch (e: Exception) {
            Napier.e("Failed to delete all activity list", e)
            throw e
        }
    }

    override suspend fun resetActivityListSequence() {
        try {
            db.taskDao().resetActivityListSequence()
        } catch (e: Exception) {
            Napier.e("Failed to reset activity list sequence", e)
            throw e
        }
    }
}
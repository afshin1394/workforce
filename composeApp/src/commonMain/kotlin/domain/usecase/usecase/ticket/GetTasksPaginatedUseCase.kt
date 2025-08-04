package domain.usecase.usecase.ticket

import domain.mappers.toTaskDomainList
import domain.models.task.TaskDomain
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase

data class PaginationParams(
    val page: Int,
    val pageSize: Int,
    val searchQuery: String? = null
)

data class PaginatedTasksResult(
    val tasks: List<TaskDomain>,
    val totalCount: Int,
    val currentPage: Int,
    val totalPages: Int,
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean
)

class GetTasksPaginatedUseCase(
    private val iTaskRepository: ITaskRepository,
) : BaseUseCase<PaginatedTasksResult, PaginationParams>() {
    
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
    
    override suspend fun run(params: PaginationParams): PaginatedTasksResult {
        val page = maxOf(0, params.page)
        val pageSize = if (params.pageSize > 0) params.pageSize else DEFAULT_PAGE_SIZE
        val offset = page * pageSize
        
        val tasks = if (params.searchQuery.isNullOrBlank()) {
            iTaskRepository.getTasksPaginated(pageSize, offset)
        } else {
            iTaskRepository.searchTasksPaginated(params.searchQuery, pageSize, offset)
        }
        
        val totalCount = if (params.searchQuery.isNullOrBlank()) {
            iTaskRepository.getTaskCount()
        } else {
            iTaskRepository.getSearchTaskCount(params.searchQuery)
        }
        
        val totalPages = if (totalCount == 0) 0 else (totalCount + pageSize - 1) / pageSize
        
        return PaginatedTasksResult(
            tasks = tasks.toTaskDomainList(),
            totalCount = totalCount,
            currentPage = page,
            totalPages = totalPages,
            hasNextPage = page < totalPages - 1,
            hasPreviousPage = page > 0
        )
    }
}
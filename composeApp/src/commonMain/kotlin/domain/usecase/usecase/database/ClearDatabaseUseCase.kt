package domain.usecase.usecase.database

import domain.repository.ITaskRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier

class ClearDatabaseUseCase(
    private val taskRepository: ITaskRepository,
    private val stepsRepository: IStepsRepository
) : BaseUseCase<Boolean, Unit>() {
    
    override suspend fun run(params: Unit): Boolean {
        return try {
            Napier.log(LogLevel.INFO, tag = "ClearDatabaseUseCase", message = "Starting database cleanup")
            
            // Clear all tasks
            taskRepository.deleteAll()
            taskRepository.deleteAllActivityList()
            
            // Clear all steps (provide empty list since deleteAll requires ticket numbers)
            stepsRepository.deleteAll(emptyList())
            
            Napier.log(LogLevel.INFO, tag = "ClearDatabaseUseCase", message = "Database cleared successfully")
            true
        } catch (e: Exception) {
            Napier.e("Failed to clear database", e)
            false
        }
    }
}
package domain.usecase.usecase.database

import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase

class CheckDatabaseDataUseCase(
    private val taskRepository: ITaskRepository
) : BaseUseCase<Boolean, Unit>() {
    
    override suspend fun run(params: Unit): Boolean {
        val tasks = taskRepository.getAll()
        val hasData = tasks.isNotEmpty()
        
        io.github.aakira.napier.Napier.log(
            io.github.aakira.napier.LogLevel.INFO,
            tag = "CheckDatabaseDataUseCase",
            message = "Database check: Found ${tasks.size} tasks, hasData = $hasData"
        )
        
        return hasData
    }
}
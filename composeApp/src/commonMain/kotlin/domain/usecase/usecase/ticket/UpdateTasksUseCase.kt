package domain.usecase.usecase.ticket

import arrow.core.Either
import data.network.response.task.InitialForm
import domain.mappers.toTaskDomainList
import domain.mappers.toTaskEntityList
import domain.models.TaskDomain
import domain.repository.IInitialFormRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.db.InitialFormEntity
import kotlinx.coroutines.delay
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class UpdateTasksUseCase(
    private val iTaskRepository: ITaskRepository,
    private val iInitialFormRepository: IInitialFormRepository
) : BaseUseCase<List<TaskDomain>, Unit>() {
    override suspend fun run(params: Unit): List<TaskDomain> {
        val tasks = iTaskRepository.fetchWorks()
        val initialTasks = arrayListOf<InitialFormEntity>()
        tasks.forEach {
            it.initial_form?.let {initialForm->
                    val jsonString = Json.encodeToString(InitialForm.serializer(), initialForm)
                    initialTasks.add(InitialFormEntity(it.wi_id,jsonString))
            }
        }
        iInitialFormRepository.deleteAll()
        iInitialFormRepository.insertAll(initialTasks)
        iTaskRepository.deleteAll()
        iTaskRepository.insertAll(tasks.toTaskEntityList())
        val domainList = iTaskRepository.getAll().toTaskDomainList()
        Napier.log(LogLevel.ASSERT,tag = "domainList", message =  domainList.toString())
        return iTaskRepository.getAll().toTaskDomainList()
    }
}
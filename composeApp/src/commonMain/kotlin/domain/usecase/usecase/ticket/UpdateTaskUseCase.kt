package domain.usecase.usecase.ticket

import data.network.response.task.FormStruct
import database.entity.InitialFormEntity
import domain.mappers.toTaskDomainList
import domain.mappers.toTaskEntityList
import domain.repository.IInitialFormRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.serialization.json.Json

class UpdateTaskUseCase (
    private val iTaskRepository: ITaskRepository,
    private val iInitialFormRepository: IInitialFormRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        val tasks = iTaskRepository.fetchWorks()
        val initialTasks = arrayListOf<InitialFormEntity>()
        tasks.details.forEach {
            it.initial_form?.let {initialForm->
                val jsonString = Json.encodeToString(FormStruct.serializer(), initialForm)
                initialTasks.add(InitialFormEntity(it.basic_info.ticket_number?:"",jsonString))
            }
        }
        iInitialFormRepository.deleteAll()
        iInitialFormRepository.resetEntitySequence()
        iInitialFormRepository.insertAll(initialTasks)
        iTaskRepository.deleteAll()
        iTaskRepository.resetEntitySequence()
        iTaskRepository.insertAll(tasks.details.toTaskEntityList())
        val domainList = iTaskRepository.getAll().toTaskDomainList()
        Napier.log(LogLevel.ASSERT,tag = "domainList", message =  domainList.toString())

    }
}
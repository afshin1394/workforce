package domain.usecase.usecase.ticket

import database.entity.InitialFormEntity
import database.entity.SendStepsEntity
import database.entity.StepPointerEntity
import database.entity.StepsEntity
import database.entity.TaskEntity
import domain.mappers.toTaskDomainList
import domain.mappers.toTaskEntityList
import domain.repository.IInitialFormRepository
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import toSendStepEntity
import toStepDetailsEntity

class UpdateTaskUseCase(
    private val iTaskRepository: ITaskRepository,
    private val iInitialFormRepository: IInitialFormRepository,
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository
) : BaseUseCase<List<TaskEntity>, Unit>() {
    override suspend fun run(params: Unit): List<TaskEntity> {
        val tasks = iTaskRepository.fetchWorks()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = tasks.toString())
        val initialTasks = arrayListOf<InitialFormEntity>()
        tasks.details.forEach {
            it.initial_form?.let { _ ->
                initialTasks.add(
                    InitialFormEntity(
                        ticket_number = it.basic_info.ticket_number ?: "",
                        initFormList = it.initial_form
                    )
                )
            }
        }
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = initialTasks.toString())

        iInitialFormRepository.deleteAll()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "deleteAll")

        iInitialFormRepository.resetEntitySequence()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "resetEntitySequence")

        iInitialFormRepository.insertAll(initialTasks)

        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "insertAll")

        iTaskRepository.deleteAll()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "deleteAll")

        iTaskRepository.resetEntitySequence()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "resetEntitySequence")

        iTaskRepository.insertAll(tasks.details.toTaskEntityList())
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "insertAll")

        val domainList = iTaskRepository.getAll().toTaskDomainList()

        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "domainList")

        Napier.log(LogLevel.ASSERT, tag = "domainList", message = domainList.toString())


        val editedTickets = try {
            iSendStepsRepository.getEditedTickets()
        } catch (_: Exception) {
            arrayListOf()
        }

        val stepEntities = arrayListOf<StepsEntity>()
        val stepPointerEntities = arrayListOf<StepPointerEntity>()
        domainList.forEach { task ->
            task.basic_info.ticket_number?.let { ticketNumber ->
                try {
                    val stepList = iStepsRepository.fetch(task.basic_info.ticket_number)
                    stepEntities.addAll(stepList.toStepDetailsEntity(ticketNumber))
                    stepPointerEntities.add(
                        StepPointerEntity(
                            ticketNumber = ticketNumber,
                            activeActivity = 0,
                            edited = false
                        )
                    )
                } catch (e: Exception) {
                    Napier.log(LogLevel.ASSERT, tag = "exceotuon", message = e.toString())
                }
            }
        }
//        val availableTasks = iTaskRepository.getAll().toTaskDomainList()
//        val editedAvailableTickets = availableTasks
//            .filter { it.basic_info.ticket_number != null && it.basic_info.ticket_number in editedTickets }
//            .map { it.basic_info.ticket_number!! }
//            .toHashSet().toList()
        iStepsRepository.deleteAll(editedTickets)
        iStepsRepository.resetEntitySequence()
        iStepsRepository.insertAll(
            getInsertingValues(
                editedTickets,
                stepEntities.sortedBy { it.activityId })
        )
        iSendStepsRepository.deleteAll(editedTickets)
        iSendStepsRepository.resetEntitySequence()
        iSendStepsRepository.insertAll(
            getSendInsertingValues(
                editedTickets,
                stepEntities.sortedBy { it.activityId }.toSendStepEntity()
            )
        )
        iStepPointerRepository.deleteAll(editedTickets)
        iStepPointerRepository.resetEntitySequence()
        iStepPointerRepository.insertAll(
            getInsertingPointerValues(
                editedTickets,
                stepPointerEntities
            )
        )

        return tasks.details.toTaskEntityList()
    }

    private fun getInsertingPointerValues(
        editedTicketNumbers: List<String>,
        stepPointerEntities: List<StepPointerEntity>
    ) = stepPointerEntities.filter { it.ticketNumber !in editedTicketNumbers }

    private fun getInsertingValues(
        editedTicketNumbers: List<String>,
        stepEntities: List<StepsEntity>
    ): List<StepsEntity> = stepEntities.filter { it.ticketNumber !in editedTicketNumbers

    }

    private fun getSendInsertingValues(
        editedTicketNumbers: List<String>,
        sendStepEntities: List<SendStepsEntity>
    ): List<SendStepsEntity> = sendStepEntities.filter { it.ticketNumber !in editedTicketNumbers }
}

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
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import toSendStepEntity
import toStepDetailsEntity
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UpdateTaskUseCase(
    private val iTaskRepository: ITaskRepository,
    private val iInitialFormRepository: IInitialFormRepository,
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository
) : BaseUseCase<List<TaskEntity>, Unit>() {

    private val mutex = Mutex()

    override suspend fun run(params: Unit): List<TaskEntity> {
        val tasks = iTaskRepository.fetchWorks()
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = tasks.toString())

        val editedTickets = iSendStepsRepository.getEditedTickets()

        iTaskRepository.deleteAll()
        iTaskRepository.resetEntitySequence()
        iStepPointerRepository.deleteAll(editedTickets)
        iInitialFormRepository.deleteAll()
        iInitialFormRepository.resetEntitySequence()
        iSendStepsRepository.deleteAll(editedTickets)
        iSendStepsRepository.resetEntitySequence()
        iStepsRepository.deleteAll(editedTickets)
        iStepsRepository.resetEntitySequence()

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


        iInitialFormRepository.insertAll(initialTasks)
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "Initial forms inserted")

        val domainList = tasks.details.toTaskEntityList().toTaskDomainList()
        val stepEntities = arrayListOf<StepsEntity>()
        val stepPointerEntities = arrayListOf<StepPointerEntity>()

        coroutineScope {
            val deferredStepFetches = domainList.map { task ->
                async {
                    task.basic_info.ticket_number?.let { ticketNumber ->
                        try {
                            val stepList = iStepsRepository.fetch(ticketNumber)
                            mutex.withLock {
                                stepEntities.addAll(stepList.toStepDetailsEntity(ticketNumber))
                                stepList.stepDetails.firstOrNull()?.acitivities?.firstOrNull()?.id?.let {
                                    stepPointerEntities.add(
                                        StepPointerEntity(
                                            ticketNumber = ticketNumber,
                                            activeActivity = it,
                                            edited = false
                                        )
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            Napier.log(LogLevel.ASSERT, tag = "exception", message = e.toString())
                        }
                    }
                }
            }

            deferredStepFetches.awaitAll()
        }

        updateDatabaseWithStepsData(
            editedTickets = iSendStepsRepository.getEditedTickets(),
            stepEntities,
            stepPointerEntities
        )

        val uniqueTasks = tasks.details.toTaskEntityList().distinctBy { it.ticket_number }
        iTaskRepository.insertAll(uniqueTasks)
        Napier.log(LogLevel.ASSERT, tag = "UpdateTaskUseCase", message = "Unique tasks inserted")

        return uniqueTasks
    }

    private suspend fun updateDatabaseWithStepsData(
        editedTickets: List<String>,
        stepEntities: List<StepsEntity>,
        stepPointerEntities: List<StepPointerEntity>
    ) {

        iStepsRepository.insertAll(
            getInsertingValues(
                editedTickets,
                stepEntities.sortedBy { it.activityId })
        )

        iSendStepsRepository.insertAll(
            getSendInsertingValues(
                editedTickets,
                stepEntities.sortedBy { it.activityId }.toSendStepEntity()
            )
        )

        iStepPointerRepository.resetEntitySequence()
        iStepPointerRepository.insertAll(
            getInsertingPointerValues(
                editedTickets,
                stepPointerEntities
            )
        )
    }

    private fun getInsertingPointerValues(
        editedTicketNumbers: List<String>,
        stepPointerEntities: List<StepPointerEntity>
    ) = stepPointerEntities.filter { it.ticketNumber !in editedTicketNumbers }

    private fun getInsertingValues(
        editedTicketNumbers: List<String>,
        stepEntities: List<StepsEntity>
    ): List<StepsEntity> = stepEntities.filter {
        it.ticketNumber !in editedTicketNumbers
    }

    private fun getSendInsertingValues(
        editedTicketNumbers: List<String>,
        sendStepEntities: List<SendStepsEntity>
    ): List<SendStepsEntity> = sendStepEntities.filter { it.ticketNumber !in editedTicketNumbers }
}


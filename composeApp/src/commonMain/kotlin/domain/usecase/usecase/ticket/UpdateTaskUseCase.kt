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
import domain.repository.ITicketRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import toSendStepEntity
import toStepDetailsEntity
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import utils.processInParallel

class UpdateTaskUseCase(
    private val iTaskRepository: ITaskRepository,
    private val iInitialFormRepository: IInitialFormRepository,
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository,
) : BaseUseCase<List<TaskEntity>, Unit>() {

    override suspend fun run(params: Unit): List<TaskEntity> {
        val tasks = iTaskRepository.fetchWorks()

        val initialTasks = tasks.details
            .mapNotNull { task ->
                task.initial_form?.let {
                    InitialFormEntity(
                        ticket_number = task.basic_info.ticket_number ?: "",
                        initFormList = it
                    )
                }
            }

        val domainList = tasks.details.toTaskEntityList().toTaskDomainList()
        val stepEntities = mutableListOf<StepsEntity>()
        val stepPointerEntities = mutableListOf<StepPointerEntity>()

        processInParallel(
            items = domainList,
            processBlock = { task, mutex ->
                task.basic_info.ticket_number?.let { ticketNumber ->
                    val stepList = iStepsRepository.fetch(ticketNumber)
                    val localStepEntities = stepList.toStepDetailsEntity(ticketNumber)
                    val activeActivityId = stepList.stepDetails.firstOrNull()
                        ?.acitivities?.firstOrNull()?.id
                        stepEntities.addAll(localStepEntities)
                        activeActivityId?.let {
                            stepPointerEntities.add(
                                StepPointerEntity(
                                    ticketNumber = ticketNumber,
                                    activeActivity = it,
                                    edited = false
                                )
                            )
                        }
                }
            }
        )

        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "3")


        iTaskRepository.deleteAll()
        iTaskRepository.resetEntitySequence()

        val uniqueTasks = tasks.details.toTaskEntityList().distinctBy { it.ticket_number }
        iTaskRepository.insertAll(uniqueTasks)

        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "4")

        iInitialFormRepository.deleteAll()
        iInitialFormRepository.resetEntitySequence()
        iInitialFormRepository.insertAll(initialTasks)

        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "5")

        updateDatabaseWithStepsData(
            editedTickets = iSendStepsRepository.getEditedTickets(),
            stepEntities,
            stepPointerEntities
        )

        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "6")


        return uniqueTasks
    }

    private suspend fun updateDatabaseWithStepsData(
        editedTickets: List<String>,
        stepEntities: List<StepsEntity>,
        stepPointerEntities: List<StepPointerEntity>
    ) {
        withContext(Dispatchers.IO) {
            iStepsRepository.apply {
                deleteAll(editedTickets)
                resetEntitySequence()
                insertAll(
                    getInsertingValues(editedTickets, stepEntities.sortedBy { it.activityId })
                )
            }

            iSendStepsRepository.apply {
                deleteAll(editedTickets)
                resetEntitySequence()
                insertAll(
                    getSendInsertingValues(
                        editedTickets,
                        stepEntities.sortedBy { it.activityId }.toSendStepEntity()
                    )
                )
            }

            iStepPointerRepository.apply {
                deleteAll(editedTickets)
                resetEntitySequence()
                insertAll(
                    getInsertingPointerValues(editedTickets, stepPointerEntities)
                )
            }
        }
    }

    private fun getInsertingPointerValues(
        editedTicketNumbers: List<String>,
        stepPointerEntities: List<StepPointerEntity>
    ) = stepPointerEntities.filter { it.ticketNumber !in editedTicketNumbers }

    private fun getInsertingValues(
        editedTicketNumbers: List<String>,
        stepEntities: List<StepsEntity>
    ): List<StepsEntity> = stepEntities.filter { it.ticketNumber !in editedTicketNumbers }

    private fun getSendInsertingValues(
        editedTicketNumbers: List<String>,
        sendStepEntities: List<SendStepsEntity>
    ): List<SendStepsEntity> = sendStepEntities.filter { it.ticketNumber !in editedTicketNumbers }
}


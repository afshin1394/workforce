package domain.usecase.usecase.ticket

import data.network.response.task.activity.ActivityListResponse
import database.entity.InitialFormEntity
import database.entity.SendStepsEntity
import database.entity.StepPointerEntity
import database.entity.StepsEntity
import database.entity.TaskEntity
import domain.mappers.toEntityList

import domain.mappers.toTaskDomainList
import domain.mappers.toTaskEntity
import domain.mappers.toTaskEntityList
import domain.models.task.ActivityListDomain
import domain.models.task.TaskDomain
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
        val ticketAllMiniList = iTaskRepository.fetchTicketAllMini()
        val activityResponseList: List<ActivityListResponse> = iTaskRepository.fetchActivityList()
        val activityDomainList: List<ActivityListDomain> = activityResponseList.map { response ->
            ActivityListDomain(
                id = response.id,
                title = response.title,
                instancePrefix = "NoN"
            )
        }
        val tasks = iTaskRepository.fetchWorks()

        Napier.log(LogLevel.ASSERT, tag = "CallApi  task   stepsYOMMMM", message = tasks.details.toString())
        val initialTasks = tasks.details
            .mapNotNull { task ->
                task.instance__tickets__basic_information__values?.let {newModel ->
                    InitialFormEntity(
                        ticket_number = task.instance__tickets__number ?: "",
                        initFormList = newModel
                    )
                }
            }

        val domainList = tasks.details.toTaskEntityList().toTaskDomainList()



        val domainListWithPrefix = domainList.map { domain ->
            val prefix = ticketAllMiniList
                .firstOrNull { mini ->
                    mini.pk == domain.ticket_type_id }
                ?.instancePrefix

            domain.copy(instancePrefix = prefix?:"")
        }

        val stepEntities = mutableListOf<StepsEntity>()
        val stepPointerEntities = mutableListOf<StepPointerEntity>()

        val existingEditedTickets = iSendStepsRepository.getEditedTickets()
        val filteredExistingEditedTickets = existingEditedTickets.filterNot { ticketNumber ->
            val task = domainListWithPrefix.find { it.ticket_number == ticketNumber }
            val existingTask = task?.let { iTaskRepository.getTaskByTicketNumber(it.ticket_number) }
            existingTask != null && existingTask.activity_id != task.activity_id
        }


     /*   val stepList = iStepsRepository.fetch("NWG-PRO-CRE-20250104-00028")
        println(" CallApi  step Testtt     ${"NWG-PRO-CRE-20250104-00028"} ${stepList}")*/

       processInParallel(
            items = domainListWithPrefix,
            processBlock = { task, mutex ->
                task.ticket_number?.let { ticketNumber ->
                    val stepList = iStepsRepository.fetch(ticketNumber)
                    Napier.log(LogLevel.ASSERT, tag = "CallApi  step   stepsYO", message = "1")
                    val localStepEntities = stepList.toStepDetailsEntity(ticketNumber)
                    Napier.log(LogLevel.ASSERT, tag = "CallApi  step   stepsYO0", message = stepList.toStepDetailsEntity(ticketNumber).toString())
                    val activeActivityId = stepList.detail.firstOrNull()?.activity_id
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

        Napier.log(LogLevel.ASSERT, tag = "CallApi     stepsYO", message = "2")


        iTaskRepository.deleteAll()
        iTaskRepository.resetEntitySequence()

       // val uniqueTasks = tasks.details.toTaskEntityList().distinctBy { it.ticket_number }

        val uniqueTasks = domainListWithPrefix.map { domain -> domain.toTaskEntity() }.distinctBy { it.ticket_number }

        val taskDomainMap: Map<String, TaskEntity> = uniqueTasks.associateBy { it.activity__title }
        val updatedActivityList: List<ActivityListDomain> = activityDomainList.map { activityDomain ->
            taskDomainMap[activityDomain.title]?.let { matchingTask ->
                activityDomain.copy(instancePrefix = matchingTask.instancePrefix)
            } ?: activityDomain
        }



        iTaskRepository.insertAllActivityList(updatedActivityList.toEntityList())
        iTaskRepository.insertAll(uniqueTasks)


        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "3")

        iInitialFormRepository.deleteAll()
        iInitialFormRepository.resetEntitySequence()
        iInitialFormRepository.insertAll(initialTasks)

        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "4")
        Napier.log(LogLevel.ASSERT, tag = "stepsYO77", message = filteredExistingEditedTickets.toString())

        updateDatabaseWithStepsData(
            editedTickets = filteredExistingEditedTickets,
            stepEntities,
            stepPointerEntities
        )

        Napier.log(LogLevel.ASSERT, tag = "stepsYO", message = "5")


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
    ): List<StepsEntity> = stepEntities.filter { (it.ticketNumber !in editedTicketNumbers)}

    private fun getSendInsertingValues(
        editedTicketNumbers: List<String>,
        sendStepEntities: List<SendStepsEntity>
    ): List<SendStepsEntity> = sendStepEntities.filter { it.ticketNumber !in editedTicketNumbers }
}



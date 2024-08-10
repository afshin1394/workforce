package domain.usecase.usecase.steps

import database.entity.SendStepsEntity
import database.entity.StepPointerEntity
import database.entity.StepsEntity
import domain.mappers.toTaskDomainList
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import toSendStepEntity
import toStepDetailsEntity

class UpdateStepsUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iTaskRepository: ITaskRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        val stepEntities = arrayListOf<StepsEntity>()
        val stepPointerEntities = arrayListOf<StepPointerEntity>()
        val tasks = iTaskRepository.getAll().toTaskDomainList()
        tasks.forEach { task ->
            task.basic_info.ticket_number?.let { ticketNumber ->
                val stepList = iStepsRepository.fetch(task.basic_info.ticket_number)
                stepEntities.addAll(stepList.toStepDetailsEntity(ticketNumber,""))
                stepPointerEntities.add(StepPointerEntity(ticketNumber, 0, false))
            }
        }
        val editedTickets = iStepsRepository.getEditedTickets()
        val availableTasks = iTaskRepository.getAll().toTaskDomainList()
        val editedAvailableTickets = availableTasks
            .filter { it.basic_info.ticket_number != null && it.basic_info.ticket_number in editedTickets }
            .map { it.basic_info.ticket_number!! }
            .toHashSet().toList()
        iStepsRepository.deleteAll(editedAvailableTickets)
        iStepsRepository.resetEntitySequence()
        iStepsRepository.insertAll(getInsertingValues(editedTickets, stepEntities.sortedBy { it.activityId  }))
        iSendStepsRepository.deleteAll(editedAvailableTickets)
        iSendStepsRepository.insertAll(getSendInsertingValues(editedTickets, stepEntities.sortedBy { it.activityId  }.toSendStepEntity()))
        iStepPointerRepository.deleteAll(editedAvailableTickets)
        iStepPointerRepository.insertAll(stepPointerEntities)

    }

    private fun getInsertingValues(
        editedTicketNumbers: List<String>,
        stepEntities: List<StepsEntity>
    ): List<StepsEntity> = stepEntities.filter { it.ticketNumber !in editedTicketNumbers }

    private fun getSendInsertingValues(
        editedTicketNumbers: List<String>,
        sendStepEntities: List<SendStepsEntity>
    ): List<SendStepsEntity> = sendStepEntities.filter { it.ticketNumber !in editedTicketNumbers }


}
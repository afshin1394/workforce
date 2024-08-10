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
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import toSendStepEntity
import toStepDetailsEntity

class UpdateStepsUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iTaskRepository: ITaskRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iSendStepsRepository: ISendStepsRepository
) : BaseUseCase<Unit, Unit>() {
    override suspend fun run(params: Unit) {
        val editedTickets = iSendStepsRepository.getEditedTickets()

        val stepEntities = arrayListOf<StepsEntity>()
        val stepPointerEntities = arrayListOf<StepPointerEntity>()
        val tasks = iTaskRepository.getAll().toTaskDomainList()
        tasks.forEach { task ->
            task.basic_info.ticket_number?.let { ticketNumber ->
                try {
                    val stepList = iStepsRepository.fetch(task.basic_info.ticket_number)
                    stepEntities.addAll(stepList.toStepDetailsEntity(ticketNumber, ""))
                    stepPointerEntities.add(StepPointerEntity(ticketNumber, 0, false))
                }catch (e:Exception){
                    Napier.log(LogLevel.ASSERT, tag = "exceotuon", message = e.toString())
                }
            }
        }
//        val availableTasks = iTaskRepository.getAll().toTaskDomainList()
//        val editedAvailableTickets = availableTasks
//            .filter { it.basic_info.ticket_number != null && it.basic_info.ticket_number in editedTickets }
//            .map { it.basic_info.ticket_number!! }
//            .toHashSet().toList()
        Napier.log(LogLevel.ASSERT,tag="editeete", message = editedTickets.toString())
        iStepsRepository.deleteAll(editedTickets)
        iStepsRepository.resetEntitySequence()
        iStepsRepository.insertAll(getInsertingValues(editedTickets, stepEntities.sortedBy { it.activityId  }))
        iSendStepsRepository.deleteAll(editedTickets)
        iSendStepsRepository.resetEntitySequence()
        iSendStepsRepository.insertAll(getSendInsertingValues(editedTickets, stepEntities.sortedBy { it.activityId  }.toSendStepEntity()))
        iStepPointerRepository.deleteAll(editedTickets)
        iStepPointerRepository.resetEntitySequence()
        iStepPointerRepository.insertAll(getInsertingPointerValues(editedTickets,stepPointerEntities))

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
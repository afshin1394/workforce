package domain.usecase.usecase.steps

import domain.mappers.toTaskDomainList
import domain.models.steps.StepDetailDomain
import domain.repository.IStepsRepository
import domain.repository.ITaskRepository
import domain.usecase.BaseUseCase
import toStepDetails

class UpdateStepsUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iTaskRepository: ITaskRepository
) : BaseUseCase<List<StepDetailDomain>, Unit>() {
    override suspend fun run(params: Unit): List<StepDetailDomain> {

        val tasks  = iTaskRepository.getAll().toTaskDomainList()
        tasks.forEach {task->
            task.basic_info.ticket_number?.let {
               val stepDetails = iStepsRepository.fetch(task.basic_info.ticket_number).toStepDetails()
            }
        }
        return emptyList()

    }

}
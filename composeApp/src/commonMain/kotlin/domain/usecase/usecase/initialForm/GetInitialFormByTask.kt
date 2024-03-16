package domain.usecase.usecase.initialForm

import data.network.response.task.InitialForm
import domain.mappers.toInitialFormDomain
import domain.models.initialForm.InitialFormDomain
import domain.repository.IInitialFormRepository
import domain.usecase.BaseUseCase

class GetInitialFormByTask(
    private val iIInitialFormRepository: IInitialFormRepository
) : BaseUseCase<InitialFormDomain, Long>() {
    override suspend fun run(params: Long): InitialFormDomain {
       return iIInitialFormRepository.getInitialFormByTaskId(params).toInitialFormDomain()
    }
}
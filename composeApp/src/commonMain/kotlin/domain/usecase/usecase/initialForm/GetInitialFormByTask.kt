package domain.usecase.usecase.initialForm

import data.network.response.task.InitialForm
import domain.mappers.toInitialFormDomain
import domain.models.initialForm.ComponentDomain
import domain.models.initialForm.InitialFormDomain
import domain.models.initialForm.ValueDomain
import domain.repository.IInitialFormRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import utils.FormViewerTypes

class GetInitialFormByTask(
    private val iIInitialFormRepository: IInitialFormRepository
) : BaseUseCase<InitialFormDomain, Long>() {
    override suspend fun run(params: Long): InitialFormDomain {
        return iIInitialFormRepository.getInitialFormByTaskId(params).toInitialFormDomain()
    }
}
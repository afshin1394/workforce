package domain.usecase.usecase.mokSteps

import data.network.response.task.FormStruct
import domain.mappers.toComponent
import domain.models.form_struct.ComponentDomain
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import kotlinx.serialization.json.Json


class StoreStepFormUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository
) : BaseUseCase<Unit, Pair<String, List<ComponentDomain>>>() {
    override suspend fun run(params: Pair<String, List<ComponentDomain>>) {

        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)

        iStepsRepository.updateFormStructure(
            params.first, stepPointerDomain.activeActivity, Json.encodeToString(
                FormStruct.serializer(), FormStruct(components = (params.second.toComponent()))
            )
        )
    }
}
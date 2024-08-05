package domain.usecase.usecase.steps

import domain.models.form_struct.ComponentDomain
import domain.repository.ISendStepsRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import toActivityDomain
import utils.toJson

//class StoreKeyValueUseCase(
//    private val iStepPointerRepository: IStepPointerRepository,
//    private val iStepsRepository: IStepsRepository,
//    private val iSendStepsRepository: ISendStepsRepository
//) : BaseUseCase<Unit, String>() {
//    override suspend fun run(params: String) {
//
//
//}
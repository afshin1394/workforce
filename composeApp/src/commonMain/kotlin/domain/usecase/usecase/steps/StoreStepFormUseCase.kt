package domain.usecase.usecase.steps

import data.network.response.task.FormStruct
import domain.mappers.toComponent
import domain.mappers.toPhotoEntityList
import domain.models.PhotoDomain
import domain.models.form_struct.ComponentDomain
import domain.repository.IPhotoRepository
import domain.repository.IStepPointerRepository
import domain.repository.IStepsRepository
import domain.usecase.BaseUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoreStepFormUseCase(
    private val iStepsRepository: IStepsRepository,
    private val iStepPointerRepository: IStepPointerRepository,
    private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, Triple<String, List<ComponentDomain>,List<PhotoDomain>>>() {
    override suspend fun run(params: Triple<String, List<ComponentDomain>,List<PhotoDomain>>) {

        val stepPointerDomain = iStepPointerRepository.getActiveActivityByTicketNumber(params.first)

        iStepsRepository.updateFormStructure(
            params.first, stepPointerDomain.activeActivity, Json.encodeToString(
                FormStruct.serializer(), FormStruct(components = (params.second.toComponent()))
            ), Json.encodeToString(params.third)
        )
//
//        params.third.forEach {
//            iPhotoRepository.deleteByKey(it.component_key)
//        }
//        iPhotoRepository.insertAll(params.third.toPhotoEntityList())
    }
}
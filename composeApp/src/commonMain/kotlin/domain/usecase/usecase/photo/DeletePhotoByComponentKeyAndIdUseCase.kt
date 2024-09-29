package domain.usecase.usecase.photo

import domain.models.DeletePhotoByComponentIdAndKeyModel
import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class DeletePhotoByComponentKeyAndIdUseCase(  private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, DeletePhotoByComponentIdAndKeyModel>() {
    override suspend fun run(params: DeletePhotoByComponentIdAndKeyModel) {
        iPhotoRepository.deleteByComponentIdAndKey(params)
    }



}
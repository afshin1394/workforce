package domain.usecase.usecase.photo


import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class DeleteByComponentKeyUseCase(  private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, String>() {
    override suspend fun run(params: String) {
        iPhotoRepository.deleteByKey(params)
    }



}
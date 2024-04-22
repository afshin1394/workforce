package domain.usecase.usecase.photo


import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class DeleteByComponentKeyUseCase(  private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, Long>() {
    override suspend fun run(params: Long) {
        iPhotoRepository.deleteByComponentKey(params)
    }



}
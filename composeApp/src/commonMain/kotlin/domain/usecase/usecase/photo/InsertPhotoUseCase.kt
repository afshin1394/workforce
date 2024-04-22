package domain.usecase.usecase.photo

import domain.mappers.toPhotoEntity
import domain.models.PhotoDomain
import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class InsertPhotoUseCase (   private val iPhotoRepository: IPhotoRepository
) : BaseUseCase<Unit, PhotoDomain>() {
    override suspend fun run(params: PhotoDomain) {
        iPhotoRepository.insert(params.toPhotoEntity())
    }

}
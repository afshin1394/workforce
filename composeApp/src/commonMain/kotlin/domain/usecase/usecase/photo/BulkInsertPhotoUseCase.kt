package domain.usecase.usecase.photo

import domain.mappers.toPhotoEntityList
import domain.models.PhotoDomain
import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class BulkInsertPhotoUseCase(private val iPhotoRepository: IPhotoRepository) : BaseUseCase<Unit, List<PhotoDomain>>() {
    override suspend fun run(params: List<PhotoDomain>) {
        iPhotoRepository.insertAll(params.toPhotoEntityList())
    }
}
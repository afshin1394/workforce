package domain.usecase.usecase.photo

import domain.mappers.toPhotoDomainList
import domain.models.PhotoDomain
import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class GetPhotoByComponentKeyUseCase  (private val iPhotoRepository: IPhotoRepository)
    : BaseUseCase<List<PhotoDomain>, String>() {

    override suspend fun run(params: String): List<PhotoDomain> {
        return iPhotoRepository.getPhotoListByKey(params).toPhotoDomainList()
    }




}
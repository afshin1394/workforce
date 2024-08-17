package domain.usecase.usecase.photo

import database.entity.PhotoEntity
import domain.mappers.toPhotoDomainList
import domain.models.PhotoDomain
import domain.repository.IPhotoRepository
import domain.usecase.BaseUseCase

class GetTicketProcessPhotos(private val iPhotoRepository: IPhotoRepository) :
    BaseUseCase<List<PhotoDomain>,  Pair<String,List<String>>>() {
    override suspend fun run(params: Pair<String,List<String>>): List<PhotoDomain> {
      return iPhotoRepository.getTicketProcessPhotos(params.first,params.second).toPhotoDomainList()
    }
}
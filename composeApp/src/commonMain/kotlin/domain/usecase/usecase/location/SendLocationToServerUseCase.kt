package domain.usecase.usecase.location


import domain.mappers.toLiveLocationRequestList
import domain.models.LiveLocationDomain
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase


class SendLocationToServerUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository
)  : BaseUseCase<Unit, List<LiveLocationDomain>>(){
    override suspend fun run(params: List<LiveLocationDomain>) {
        iGeneralLocationRepository.sendLocationToServer(params.toLiveLocationRequestList())
        iGeneralLocationRepository.updateUnSend()
        iGeneralLocationRepository.deleteSent()
    }
}
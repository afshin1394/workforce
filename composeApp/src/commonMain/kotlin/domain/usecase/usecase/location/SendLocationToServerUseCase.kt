package domain.usecase.usecase.location


import domain.mappers.toListLiveLocationRequest
import domain.mappers.toLiveLocationRequestList
import domain.models.LiveLocationDomain
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase


class SendLocationToServerUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository
)  : BaseUseCase<Unit, Unit>(){
    override suspend fun run(params: Unit) {
        val locations = iGeneralLocationRepository.selectUnSend().toListLiveLocationRequest()
        iGeneralLocationRepository.sendLocationToServer(locations)
        iGeneralLocationRepository.updateUnSend()
        iGeneralLocationRepository.deleteSent()
    }
}
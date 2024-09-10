package domain.usecase.usecase.location


import domain.mappers.toListLiveLocationRequest
import domain.mappers.toLiveLocationRequestList
import domain.models.LiveLocationDomain
import domain.repository.IGeneralLocationRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier


class SendLocationToServerUseCase(
    private val iGeneralLocationRepository: IGeneralLocationRepository
)  : BaseUseCase<Unit, Unit>(){
    override suspend fun run(params: Unit) {
        val locations = iGeneralLocationRepository.selectUnSend().toListLiveLocationRequest()
        Napier.log(LogLevel.ASSERT, tag = "SendLocationToServerUseCase", message = locations.toString())
        iGeneralLocationRepository.sendLocationToServer(locations)
        iGeneralLocationRepository.updateUnSend()
        iGeneralLocationRepository.deleteSent()
    }
}
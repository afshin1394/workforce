package domain.usecase.usecase.availability

import data.AvailabilityRepositoryImpl
import data.network.request.ChangeAvailabilityRequest
import domain.repository.IAvailabilityRepository
import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.getSharedPref
import utils.Availability
import utils.AvailabilityObjectId

class ChangeServerAvailabilityUseCase(
    private val iAvailabilityRepository: IAvailabilityRepository
) : BaseUseCase<String,Boolean>() {
    override suspend fun run(params: Boolean) : String  {
       val available = getSharedPref().getBool(Availability,false)
       val changeAvailabilityRequest = if (available){
            ChangeAvailabilityRequest(false, getSharedPref().getString(AvailabilityObjectId)?.toInt())
        }else{
            ChangeAvailabilityRequest(true)
        }

       return iAvailabilityRepository.changeAvailability(changeAvailabilityRequest)
    }
}
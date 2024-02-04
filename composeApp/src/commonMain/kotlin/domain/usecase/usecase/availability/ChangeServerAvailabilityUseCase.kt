package domain.usecase.usecase.availability

import data.AvailabilityRepositoryImpl
import data.network.ChangeAvailabilityRequest
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.Availability
import utils.AvailabilityObjectId

class ChangeServerAvailabilityUseCase(
    private val availabilityRepository : AvailabilityRepositoryImpl
) : BaseUseCase<Unit,Boolean>() {
    override suspend fun run(params: Boolean) {
       val available = getSharedPref().getBool(Availability,false)
       val changeAvailabilityRequest = if (available){
            ChangeAvailabilityRequest(false, getSharedPref().getInt(AvailabilityObjectId,0))
        }else{
            ChangeAvailabilityRequest(true)
        }
        availabilityRepository.changeAvailability(changeAvailabilityRequest)
    }
}
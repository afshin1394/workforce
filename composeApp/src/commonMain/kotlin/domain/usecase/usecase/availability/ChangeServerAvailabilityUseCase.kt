package domain.usecase.usecase.availability

import data.network.request.ChangeAvailabilityRequest
import domain.repository.IAvailabilityRepository
import domain.usecase.BaseUseCase
import io.ktor.http.HttpStatusCode
import irancell.nwg.wfm.getSharedPref
import utils.Availability
import utils.AvailabilityObjectId

class ChangeServerAvailabilityUseCase(
    private val iAvailabilityRepository: IAvailabilityRepository
) : BaseUseCase<Boolean,Unit>() {
    override suspend fun run(params: Unit) :  Boolean  {
       val available = getSharedPref().getBool(Availability,false)
       val changeAvailabilityRequest = if (available){
            ChangeAvailabilityRequest(false, getSharedPref().getString(AvailabilityObjectId)?.toInt())
       }else{
            ChangeAvailabilityRequest(true)

       }
       val pair = iAvailabilityRepository.changeAvailability(changeAvailabilityRequest)
        if(pair.first == HttpStatusCode.OK){
            getSharedPref().put(Availability,!available)
        }
        if(getSharedPref().getBool(Availability,false)){
            getSharedPref().put(AvailabilityObjectId,pair.second)
        }
         return   getSharedPref().getBool(Availability,false)
    }
}
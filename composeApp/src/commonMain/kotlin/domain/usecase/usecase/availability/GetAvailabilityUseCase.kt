package domain.usecase.usecase.availability

import domain.repository.IAvailabilityRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.Availability
import utils.AvailabilityObjectId

class GetAvailabilityUseCase(private val iAvailabilityRepository: IAvailabilityRepository) : BaseUseCase<Boolean,Unit>() {
    override suspend fun run(params: Unit) : Boolean {
         val response  = iAvailabilityRepository.fetchAvailability()
         getSharedPref().put(Availability,response.detail)
         getSharedPref().put(AvailabilityObjectId,response.obj_id.toString())
         return  getSharedPref().getBool(Availability,false)
    }
}
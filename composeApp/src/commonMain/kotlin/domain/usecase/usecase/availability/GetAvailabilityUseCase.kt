package domain.usecase.usecase.availability

import domain.repository.IAvailabilityRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.Availability

class GetAvailabilityUseCase(private val iAvailabilityRepository: IAvailabilityRepository) : BaseUseCase<Boolean,Unit>() {
    override suspend fun run(params: Unit) : Boolean {
         val availability  = iAvailabilityRepository.fetchAvailability()
         getSharedPref().put(Availability,availability)
         return  getSharedPref().getBool(Availability,false)
    }
}
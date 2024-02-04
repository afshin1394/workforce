package domain.usecase.usecase.availability

import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.Availability

class GetAvailabilityUseCase : BaseUseCase<Boolean,Unit>() {
    override suspend fun run(params: Unit) : Boolean {
         return  getSharedPref().getBool(Availability,false)
    }
}
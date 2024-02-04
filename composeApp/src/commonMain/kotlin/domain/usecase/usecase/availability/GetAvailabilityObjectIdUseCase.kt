package domain.usecase.usecase.availability

import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.AvailabilityObjectId

class GetAvailabilityObjectIdUseCase : BaseUseCase<Int,Unit>() {
    override suspend fun run(params: Unit) : Int {
       return getSharedPref().getInt(AvailabilityObjectId,0)
    }
}
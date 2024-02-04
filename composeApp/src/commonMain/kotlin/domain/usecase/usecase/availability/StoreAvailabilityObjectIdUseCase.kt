package domain.usecase.usecase.availability

import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.AvailabilityObjectId

class StoreAvailabilityObjectIdUseCase : BaseUseCase<Unit,Int>(){
    override suspend fun run(params: Int) {
        getSharedPref().put(AvailabilityObjectId,params)
    }

}
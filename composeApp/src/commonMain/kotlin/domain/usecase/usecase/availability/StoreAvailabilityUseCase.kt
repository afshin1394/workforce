package domain.usecase.usecase.availability

import domain.usecase.BaseUseCase
import irancell.nwg.wfm.getSharedPref
import utils.Availability

class StoreAvailabilityUseCase : BaseUseCase<Unit,Boolean>() {
    override suspend fun run(params: Boolean) {
        getSharedPref().put(Availability,params)
    }
}
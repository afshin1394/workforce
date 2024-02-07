package domain.usecase.usecase.availability

import domain.usecase.BaseUseCase
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import irancell.nwg.wfm.getSharedPref
import utils.Availability
import utils.AvailabilityObjectId

class StoreAvailabilityUseCase : BaseUseCase<Unit,Pair<Boolean,String>>() {
    override suspend fun run(params: Pair<Boolean,String>) {
        Napier.log(LogLevel.ASSERT,"StoreAvailabilityUseCase", message = "Availability ${params.first} AvailabilityObjectId ${params.second}")
        getSharedPref().put(Availability,params.first)
        if (params.first) {
            getSharedPref().put(AvailabilityObjectId, params.second)
        }
    }
}
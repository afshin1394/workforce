package domain.usecase.usecase.suspendTask

import domain.mappers.toSuspendTaskDomain
import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase

data class SuspendTaskFootStomp(val ticketNumber : String ,val date : String, val latitude : String,val longitude : String)
class UpdateSuspendTaskDetails(
    private val iSuspendTaskRepository: ISuspendTaskRepository
) : BaseUseCase<Unit,SuspendTaskFootStomp>() {
    override suspend fun run(params: SuspendTaskFootStomp) {
        iSuspendTaskRepository.updateSuspendDetails(
                ticketNumber = params.ticketNumber,
                dateTime = params.date,
                latitude =  params.latitude,
                longitude = params.longitude
            )

    }
}
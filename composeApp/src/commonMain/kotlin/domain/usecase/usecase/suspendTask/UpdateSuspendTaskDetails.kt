package domain.usecase.usecase.suspendTask

import domain.repository.ISuspendTaskRepository
import domain.usecase.BaseUseCase
import irancell.nwg.wfm.Location
data class SuspendTaskFootStomp(val taskId : Int ,val date : String, val latitude : String,val longitude : String)
class UpdateSuspendTaskDetails(
    private val suspendTaskRepository : ISuspendTaskRepository
) : BaseUseCase<Unit,SuspendTaskFootStomp>() {
    override suspend fun run(params: SuspendTaskFootStomp) {
            suspendTaskRepository.updateSuspendDetails(
                taskId = params.taskId,
                dateTime = params.date,
                latitude =  params.latitude,
                longitude = params.longitude
            )

    }
}
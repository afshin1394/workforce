package domain.mappers

import domain.models.SuspendTaskDomain
import irancell.nwg.wfm.db.SuspendTaskEntity

fun SuspendTaskEntity.toSuspendTaskDomain() : SuspendTaskDomain{
   return SuspendTaskDomain(
      taskId = this.taskId,
      reason = this.reason,
      description =  this.description,
      attachmentsUri = this.attachmentsUri,
      isSent = this.isSent,
      datetime = this.datetime,
      latitude = this.latitude,
      longitude = this.longitude
   )
}

fun SuspendTaskDomain.toSuspendTaskEntity() : SuspendTaskEntity{
    return SuspendTaskEntity(
        taskId = this.taskId,
        reason = this.reason,
        description =  this.description,
        attachmentsUri = this.attachmentsUri,
        isSent = this.isSent,
        datetime = this.datetime,
        latitude = this.latitude,
        longitude = this.longitude
    )
}

fun List<SuspendTaskDomain>.toSuspendTaskEntityList() : List<SuspendTaskEntity>{
    return map {
        SuspendTaskEntity(
            taskId = it.taskId,
            reason = it.reason,
            description =  it.description,
            attachmentsUri = it.attachmentsUri,
            isSent = it.isSent,
            datetime = it.datetime,
            latitude = it.latitude,
            longitude = it.longitude
        )
    }.toList()
}

fun List<SuspendTaskEntity>.toSuspendTaskDomainList() : List<SuspendTaskDomain>{
    return map {
        SuspendTaskDomain(
            taskId = it.taskId,
            reason = it.reason,
            description =  it.description,
            attachmentsUri = it.attachmentsUri,
            isSent = it.isSent,
            datetime = it.datetime,
            latitude = it.latitude,
            longitude = it.longitude
        )
    }.toList()
}
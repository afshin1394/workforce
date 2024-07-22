package domain.mappers

import database.entity.SuspendTaskEntity
import domain.models.SuspendTaskDomain


fun SuspendTaskEntity.toSuspendTaskDomain() : SuspendTaskDomain{
   return SuspendTaskDomain(
      ticket_number = this.ticket_number,
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
        ticket_number = this.ticket_number,
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
       it.toSuspendTaskEntity()
    }
}

fun List<SuspendTaskEntity>.toSuspendTaskDomainList() : List<SuspendTaskDomain>{
    return map {
        it.toSuspendTaskDomain()
    }
}
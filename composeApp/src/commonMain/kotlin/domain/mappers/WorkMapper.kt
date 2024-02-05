package domain.mappers

import data.network.response.WorksNetworkResponse
import domain.models.WorkDomain

fun WorksNetworkResponse.toTaskDomain(): WorkDomain {
    return WorkDomain(
        workId = this.wi_id,
        title = this.ticket_instance_title,
        instanceId = this.ticket_instance_id,
        instanceNumber = this.ticket_instance_number,
        instanceState = this.ticket_instance_state,
        instanceTitle = this.ticket_instance_title,
    )
}

fun List<WorksNetworkResponse>.toTaskDomainList(): List<WorkDomain> {
    return map {
         WorkDomain(
            workId = it.wi_id,
            title = it.ticket_instance_title,
            instanceId = it.ticket_instance_id,
            instanceNumber = it.ticket_instance_number,
            instanceState = it.ticket_instance_state,
            instanceTitle = it.ticket_instance_title,

            )
    }.toList()
}
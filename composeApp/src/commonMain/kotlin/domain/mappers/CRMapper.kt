package domain.mappers

import data.network.response.cr.CRNetworkResponse
import domain.models.CRDomain


fun CRNetworkResponse.toCRDomain() : CRDomain {
    return CRDomain(
        workId = wi_id,
        activityId = activity_id,
        activityTitle = activity_title,
        activityState = activity_state,
        ticketTitle = ticket_title,
        ticketInstanceId = ticket_instance_id,
        ticketInstanceNumber = ticket_instance_number,
        ticketInstanceState = ticket_instance_state,
        ticketInstanceTitle = ticket_instance_title,
    )
}
fun CRDomain.toCRNetworkResponse() : CRNetworkResponse {
    return CRNetworkResponse(
        wi_id = workId,
        activity_id = activityId,
        activity_title = activityTitle,
        activity_state = activityState,
        ticket_title = ticketTitle,
        ticket_instance_id = ticketInstanceId,
        ticket_instance_number = ticketInstanceNumber,
        ticket_instance_state = ticketInstanceState,
        ticket_instance_title = ticketInstanceTitle,
        ticket_instance_action = null,
    )
}

fun List<CRDomain>.toCRNetworkResponseList() : List<CRNetworkResponse>{
    return map {
        it.toCRNetworkResponse()
    }
}

fun List<CRNetworkResponse>.toCRDomainList() : List<CRDomain>{
    return map {
        it.toCRDomain()
    }
}
package domain.mappers

import data.network.request.ticket.Phase
import data.network.request.ticket.TicketDetailRequest
import data.network.response.ticket.Objects
import data.network.response.ticket.TicketDetailResponse
import domain.models.ticket.ObjectsDomain
import domain.models.ticket.PhaseDomain
import domain.models.ticket.TicketDetailRequestDomain
import domain.models.ticket.TicketDetailResponseDomain

fun TicketDetailResponse.toTicketDetailResponseDomain() : TicketDetailResponseDomain{
    val mapOfMaps = HashMap<String,ObjectsDomain>()
    this.events.forEach {
        mapOfMaps[it.key] = (ObjectsDomain(it.value.attributes))
    }
    return TicketDetailResponseDomain(mapOfMaps)
}

fun TicketDetailResponseDomain.toTicketDetailResponse() : TicketDetailResponse{
    val mapOfMaps = HashMap<String,Objects>()
    this.events.forEach {
        mapOfMaps[it.key] = (Objects(it.value.attributes))
    }
    return TicketDetailResponse(mapOfMaps)
}


fun TicketDetailRequestDomain.toTicketDetailRequest() : TicketDetailRequest{
    return TicketDetailRequest(phase = this.phaseDomain.map { Phase(it.phase,it.field) })
}

fun TicketDetailRequest.toTicketDetailRequestDomain() : TicketDetailRequestDomain{
    return TicketDetailRequestDomain(phaseDomain = this.phase.map { PhaseDomain(it.phase,it.field) })
}
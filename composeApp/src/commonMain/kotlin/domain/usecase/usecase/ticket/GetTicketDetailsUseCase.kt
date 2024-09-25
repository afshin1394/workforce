package domain.usecase.usecase.ticket

import domain.mappers.toTicketDetailRequest
import domain.mappers.toTicketDetailResponseDomain
import domain.models.ticket.TicketDetailRequestDomain
import domain.models.ticket.TicketDetailResponseDomain
import domain.repository.ITicketRepository
import domain.usecase.BaseUseCase

class GetTicketDetailsUseCase(
    private val iTicketRepository: ITicketRepository
) : BaseUseCase<TicketDetailResponseDomain, Pair<String, TicketDetailRequestDomain>>() {
    override suspend fun run(params: Pair<String,TicketDetailRequestDomain>): TicketDetailResponseDomain {
       return iTicketRepository.fetchTicketDetails(params.first,
           params.second.toTicketDetailRequest()).toTicketDetailResponseDomain()
    }

}
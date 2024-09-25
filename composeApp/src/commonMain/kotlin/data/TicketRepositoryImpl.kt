package data

import data.network.request.ticket.TicketDetailRequest
import data.network.response.ticket.TicketDetailResponse
import domain.repository.ITicketRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

class TicketRepositoryImpl(
    private val httpClient: HttpClient,
) : ITicketRepository {
    override suspend fun fetchTicketDetails(ticketId : String,ticketDetailRequest: TicketDetailRequest): TicketDetailResponse {
       return httpClient.post("ticket-instance/${ticketId}/details/"){
            setBody(ticketDetailRequest)
        }.body<TicketDetailResponse>()
    }
}
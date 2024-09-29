package domain.repository

import data.network.request.ticket.TicketDetailRequest
import data.network.response.task.task.TasksNetworkResponse
import data.network.response.ticket.TicketDetailResponse

interface ITicketRepository {
    suspend  fun fetchTicketDetails(ticketId:String,ticketDetailRequest: TicketDetailRequest) : Map<String,String>
}
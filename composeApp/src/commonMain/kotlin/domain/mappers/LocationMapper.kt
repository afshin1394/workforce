package domain.mappers

import data.network.request.auth.LoginNetworkRequest
import data.network.request.live_location.LiveLocationRequest
import domain.models.LiveLocationDomain
import domain.models.LoginRequestDomain

fun LiveLocationRequest.toLiveLocationDomain() : LiveLocationDomain {
    return LiveLocationDomain(
        latitude = latitude, longitude = longitude, recorded_date = recorded_date,site= site,
        attendance = attendance, ticket_num = ticket_num
    )
}
fun LiveLocationDomain.toLiveLocationRequest() : LiveLocationRequest {
    return LiveLocationRequest(
        latitude = latitude, longitude = longitude, recorded_date = recorded_date,site= site,
        attendance = attendance, ticket_num = ticket_num
    )
}

fun List<LiveLocationDomain>.toLiveLocationRequestList() : List<LiveLocationRequest>{
    return map {
        it.toLiveLocationRequest()
    }
}

fun List<LiveLocationRequest>.toLiveLocationDomainList() : List<LiveLocationDomain>{
    return map {
        it.toLiveLocationDomain()
    }
}
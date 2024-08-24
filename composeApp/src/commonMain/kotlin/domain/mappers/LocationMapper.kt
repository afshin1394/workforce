package domain.mappers

import data.network.request.auth.LoginNetworkRequest
import data.network.request.live_location.LiveLocationRequest
import database.entity.GeneralLocationEntity
import domain.models.LiveLocationDomain
import domain.models.LoginRequestDomain
import irancell.nwg.wfm.getSharedPref
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import utils.AvailabilityObjectId
import utils.TicketNumber
import utils.getCurrentDate

fun LiveLocationRequest.toLiveLocationDomain() : LiveLocationDomain {
    return LiveLocationDomain(
        latitude = latitude, longitude = longitude, recorded_date = recorded_date,site= site,
        attendance = attendance, ticket_num = ticket_num, network_info = network_info
    )
}
fun LiveLocationDomain.toLiveLocationRequest() : LiveLocationRequest {
    return LiveLocationRequest(
        latitude = latitude, longitude = longitude, recorded_date = recorded_date,site= site, ticket_num = ticket_num, attendance = attendance, network_info = network_info
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

fun GeneralLocationEntity.toLiveLocationRequest() : LiveLocationRequest{
    return LiveLocationRequest(this.latitude.toDouble(),this.longitude.toDouble(), getCurrentDate(),0,  getSharedPref().getString(
        AvailabilityObjectId
    )?.toLong() ?: 0,
        getSharedPref().getString(TicketNumber) ?: "", network_info = Json.decodeFromString(JsonObject.serializer(), this.networkInfo))

}

fun List<GeneralLocationEntity>.toListLiveLocationRequest() : List<LiveLocationRequest>{
    return map{
        it.toLiveLocationRequest()
    }
}
package domain.mappers

import data.network.request.auth.LoginNetworkRequest
import data.network.response.auth.LoginNetworkResponse
import domain.models.LoginRequestDomain
import domain.models.LoginResponseDomain

fun LoginNetworkRequest.toLoginRequestDomain() : LoginRequestDomain {
    return LoginRequestDomain(
        username, password
    )
}
fun LoginRequestDomain.toLoginNetworkRequest() : LoginNetworkRequest {
    return LoginNetworkRequest(
        username, password
    )
}

fun LoginResponseDomain.toLoginNetworkResponse() : LoginNetworkResponse {
    return LoginNetworkResponse(
        session_id, phone_number
    )
}


fun LoginNetworkResponse.toLoginResponseDomain() : LoginResponseDomain{
    return LoginResponseDomain(
        session_id, phone_number
    )
}
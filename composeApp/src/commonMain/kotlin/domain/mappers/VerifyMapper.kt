package domain.mappers

import data.network.request.VerifyNetworkRequest
import data.network.response.LoginNetworkResponse
import data.network.response.VerifyNetworkResponse
import domain.models.LoginResponseDomain
import domain.models.VerifyRequestDomain
import domain.models.VerifyResponseDomain

fun VerifyNetworkResponse.toVerifyResponseDomain() : VerifyResponseDomain {
    return VerifyResponseDomain(
       auth_token
    )
}

fun VerifyResponseDomain.toVerifyNetworkResponse() : VerifyNetworkResponse {
    return VerifyNetworkResponse(
        auth_token
    )
}

fun VerifyRequestDomain.toVerifyNetworkRequest() : VerifyNetworkRequest{
    return VerifyNetworkRequest(
         session_id, code
    )
}

fun VerifyNetworkRequest.toVerifyRequestDomain() : VerifyRequestDomain{
    return VerifyRequestDomain(
        session_id, code
    )
}
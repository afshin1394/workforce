package domain.mappers

import data.network.LoginRequestNetwork
import domain.models.LoginRequestDomain

fun LoginRequestNetwork.toLoginRequestDomain() : LoginRequestDomain {
    return LoginRequestDomain(
        username, password
    )
}
fun LoginRequestDomain.toLoginRequestNetwork() : LoginRequestNetwork {
    return LoginRequestNetwork(
        username, password
    )
}
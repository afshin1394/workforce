package data.network.response

import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseNetwork(val session_id: String, val phone_number: String?)

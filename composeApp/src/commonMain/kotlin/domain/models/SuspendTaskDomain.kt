package domain.models

import kotlinx.serialization.Serializable

@Serializable
data class SuspendTaskDomain(var ticket_number: String,
                             var reason: String,
                             var description: String,
                             var attachmentsUri: String,
                             var isSent: Long,
                             var datetime: String,
                             var latitude: String,
                             var longitude: String,)




package domain.models

data class SuspendTaskDomain(val taskId: Long,
                             var reason: String,
                             var description: String,
                             val attachmentsUri: String,
                             val isSent: Long,
                             val datetime: String,
                             val latitude: String,
                             val longitude: String,)




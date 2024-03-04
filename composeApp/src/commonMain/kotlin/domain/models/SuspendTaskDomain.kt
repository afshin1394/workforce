package domain.models

data class SuspendTaskDomain(var taskId: Long,
                             var reason: String,
                             var description: String,
                             var attachmentsUri: String,
                             var isSent: Long,
                             var datetime: String,
                             var latitude: String,
                             var longitude: String,)




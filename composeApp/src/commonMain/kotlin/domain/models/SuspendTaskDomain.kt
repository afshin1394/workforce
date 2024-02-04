package domain.models

data class SuspendTaskDomain(val taskId: Long,
                             val reason: String,
                             val description: String,
                             val attachmentsUri: String,
                             val isSent: Long,
                             val datetime: String,
                             val latitude: String,
                             val longitude: String,)




package domain.models

import data.network.response.task.TaskInstanceAction
import kotlinx.serialization.SerialName

data class CRDomain(
    val workId: Long,
    val activityId: Long,
    val activityTitle: String,
    val activityState: String,
    val ticketTitle: String,
    val ticketInstanceId: Long,
    val ticketInstanceNumber: String,
    val ticketInstanceState: String,
    val ticketInstanceTitle: String,
)

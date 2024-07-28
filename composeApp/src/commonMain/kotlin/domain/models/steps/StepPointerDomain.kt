package domain.models.steps

import androidx.room.PrimaryKey

data class StepPointerDomain(
    val ticketNumber: String,
    val activeActivity: Long,
    val edited: Boolean
)
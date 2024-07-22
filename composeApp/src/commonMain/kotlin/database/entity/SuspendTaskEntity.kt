package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class SuspendTaskEntity(
    @PrimaryKey val ticket_number: String,
    val reason: String,
    val description: String,
    val attachmentsUri: String,
    val isSent: Long,
    val datetime: String,
    val latitude: String,
    val longitude: String
)
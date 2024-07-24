package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StepsEntity")
data class StepsEntity(
    val ticketNumber:String,
    val wi: String,
    val activityId: Long,
    val formStructure: String,
)

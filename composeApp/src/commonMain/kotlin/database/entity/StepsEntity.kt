package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StepsEntity")
data class StepsEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long,
    val ticketNumber:String,
    val wi: Long,
    val activityId: Long,
    val formStructure: String,
    val edited : Boolean
)

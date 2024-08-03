package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StepsEntity")
data class StepsEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long,
    val ticketNumber:String,
    val wi: Long,
    val title : String,
    val tag : Long,
    val activityId: Long,
    val formStructure: String,
    val photoList : String,
    val edited : Boolean,
    val isSent : Boolean,
)

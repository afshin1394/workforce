package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "SendStepsEntity")
data class SendStepsEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long,
    val ticketNumber:String,
    val wi: Long,
    val title : String,
    val tag : Long,
    val activityId: Long,
    val key_value_structure: String,
    val edited : Boolean
)

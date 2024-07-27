package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StepPointerEntity")
data class StepPointerEntity(
    @PrimaryKey
    val ticketNumber:String,
    val activeActivity : Long,
    val edited : Boolean
)

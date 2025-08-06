package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "StepPointerEntity",
    indices = [
        androidx.room.Index(value = ["ticketNumber"], unique = true)
    ]
)
data class StepPointerEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long = 0,
    val ticketNumber:String,
    val activeActivity : Long,
    val edited : Boolean
)

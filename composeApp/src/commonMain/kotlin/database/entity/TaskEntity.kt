package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class TaskEntity(

    @PrimaryKey(autoGenerate = true) val pk: Long = 0,
    val ticket_id: String,
    val ticket_number: String,
    val ticket_state: String,
    val level: String,
    val location: String,
    val site: String,
    val region: String,
    val province: String,
    val city: String,
)
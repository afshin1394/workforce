package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.network.response.task.task.InstanceTicketsProperties


/*@Entity
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val ticket_id: Int,
    val ticket_number: String,
    val ticket_state: String,
    val level: String,
    val location: String,
    val site: String,
    val region: String,
    val province: String,
    val city: String,
    val activity_id: Long,
    val activity__title: String,
)*/



@Entity(
    indices = [
        androidx.room.Index(value = ["ticket_number"], unique = true)
    ]
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val ticket_id: Int,
    val ticket_type_id:Int,
    val instancePrefix :String,
    val ticket_number: String,
    val ticket_state: String,
    val activity_id: Long,
    val activity__title: String,
    val properties: List<InstanceTicketsProperties>
)
package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import data.network.response.task.task.InitForm

@Entity
 data class InitialFormEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long = 0,
    val ticket_number: String,
    val initFormList: List<InitForm>
)
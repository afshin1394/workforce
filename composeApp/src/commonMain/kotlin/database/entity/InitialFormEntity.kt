package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class InitialFormEntity(
    @PrimaryKey val ticket_number: String,
    val structure: String,
)
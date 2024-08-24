package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
 data class GeneralLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: String,
    val longitude: String,
    val datetime: String,
    val isSent: Long?,
    val networkInfo : String,
)

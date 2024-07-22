package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
 data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long,
     val ticket_number: String,
     val component_key: String,
     val index_row: Long,
     val origin_uri: String,
     val edited_uri: String,
     val angle: String,
)
package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ProfileEntity (

     @PrimaryKey val pk: String,
     val username: String,
     val email: String,
     val first_name: String,
     val last_name: String,
     val company: String,
     val organization: String,
     val national_id: String,
     val phone_number: String,


)
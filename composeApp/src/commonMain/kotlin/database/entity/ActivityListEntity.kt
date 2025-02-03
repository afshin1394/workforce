package database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ActivityListEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long = 0,
    val id: Long? = 0,
    val title: String? = null,
    val instancePrefix :String="",
)
{
    override fun toString(): String {
        return "Detail(id=$id,title=$title ,instancePrefix=$instancePrefix)"
    }
}